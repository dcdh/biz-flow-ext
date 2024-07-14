package io.bizflowframework.biz.flow.ext.deployment;

import io.agroal.api.AgroalDataSource;
import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRoot;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.BaseAggregateRootRepository;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.BaseOnSavedEvent;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.DefaultCreatedAtProvider;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.api.ExceptionsMapper;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.command.AggregateCommandRequest;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.creational.AggregateIdInstanceCreator;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.creational.AggregateRootInstanceCreator;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.creational.ReflectionAggregateIdInstanceCreator;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.creational.ReflectionAggregateRootInstanceCreator;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event.AggregateRootEventPayload;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event.BaseJdbcPostgresqlEventRepository;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event.EventRepository;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event.PostgresqlInitializer;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.incrementer.DefaultAggregateVersionIncrementer;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.AggregateRootEventPayloadSerde;
import io.bizflowframework.biz.flow.ext.runtime.usecase.BizMutationUseCase;
import io.bizflowframework.biz.flow.ext.runtime.usecase.BizQueryUseCase;
import io.bizflowframework.biz.flow.ext.runtime.usecase.CommandRequest;
import io.bizflowframework.biz.flow.ext.runtime.usecase.QueryRequest;
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.arc.deployment.GeneratedBeanGizmoAdaptor;
import io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ApplicationIndexBuildItem;
import io.quarkus.deployment.builditem.BytecodeTransformerBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.gizmo.*;
import io.quarkus.resteasy.reactive.spi.CustomExceptionMapperBuildItem;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Singleton;
import org.jboss.jandex.ClassInfo;
import org.jboss.logging.Logger;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class BizFlowExtProcessor {
    private static final Logger LOGGER = Logger.getLogger(BizFlowExtProcessor.class);

    private static final String FEATURE = "biz-flow-ext";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void enhanceAggregateRootEventPayloadSerde(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                               final BuildProducer<BytecodeTransformerBuildItem> bytecodeTransformerBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(AggregateRootEventPayloadSerde.class)
                .forEach(implementor ->
                        bytecodeTransformerBuildItemProducer.produce(
                                new BytecodeTransformerBuildItem.Builder()
                                        .setClassToTransform(implementor.name().toString())
                                        .setVisitorFunction((s, classVisitor) ->
                                                new AggregateRootEventPayloadSerdeClassVisitor(classVisitor))
                                        .setCacheable(true)
                                        .build()
                        ));
    }

    @BuildStep
    void validateAtLeastOneSerdeIsImplementedPerAggregateRootAndAggregateRootEventPayload(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                                                                          final BuildProducer<ValidationErrorBuildItem> validationErrorBuildItemProducer) {
        // First get all events per aggregate
        final List<AggregateRootEventPayloadKey> aggregateRootEventPayloadKeys = applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(AggregateRootEventPayload.class)
                .stream()
                .map(new ExtractInterfaceParameterizedTypeFromClassInfo(AggregateRootEventPayload.class))
                .map(new ExtractAggregateRootEventPayloadKeyFromPayload())
                .toList();
        // Next get all serde implementations
        final List<AggregateRootEventPayloadKey> aggregateRootEventPayloadSerdeKeys = applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(AggregateRootEventPayloadSerde.class)
                .stream()
                .map(new ExtractInterfaceParameterizedTypeFromClassInfo(AggregateRootEventPayloadSerde.class))
                .map(new ExtractAggregateRootEventPayloadKeyFromSerde())
                .toList();
        // Now check the matching
        aggregateRootEventPayloadKeys.forEach(aggregateRootEventPayloadKey -> {
            if (aggregateRootEventPayloadSerdeKeys.stream().noneMatch(aggregateRootEventPayloadSerdeKey -> aggregateRootEventPayloadSerdeKey.equals(aggregateRootEventPayloadKey))) {
                validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                        new IllegalStateException(String.format("Missing Serde for AggregateRoot '%s' and AggregateEventPayload '%s'",
                                aggregateRootEventPayloadKey.aggregateRootClassName(), aggregateRootEventPayloadKey.eventPayloadClassName()))
                ));
            }
        });
    }

    @BuildStep
    void validateOnlyOneSerdeImplementationPerAggregateRootAndAggregateRootEventPayload(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                                                                        final BuildProducer<ValidationErrorBuildItem> validationErrorBuildItemProducer) {
        final Map<AggregateRootEventPayloadKey, List<ClassInfo>> serdeImplementationsByAggregateRootAndEventPayload = applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(AggregateRootEventPayloadSerde.class)
                .stream()
                .collect(Collectors.groupingBy(
                        new ExtractInterfaceParameterizedTypeFromClassInfo(AggregateRootEventPayloadSerde.class)
                                .andThen(new ExtractAggregateRootEventPayloadKeyFromSerde())
                ));
        serdeImplementationsByAggregateRootAndEventPayload
                .forEach((key, implementors) -> {
                    if (implementors.size() > 1) {
                        final String implementations = implementors.stream().map(implementor -> implementor.name().toString())
                                .sorted()
                                .collect(Collectors.joining(", "));
                        validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                                new IllegalStateException(String.format("Multiple implementations found for Serde '%s', only one is expected. Found implementations %s",
                                        key, implementations))
                        ));
                    }
                });
    }

    @BuildStep
    void enhanceBaseAggregateRootRepository(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                            final BuildProducer<BytecodeTransformerBuildItem> bytecodeTransformerBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownSubclasses(BaseAggregateRootRepository.class)
                .forEach(implementor ->
                        bytecodeTransformerBuildItemProducer.produce(
                                new BytecodeTransformerBuildItem.Builder()
                                        .setClassToTransform(implementor.name().toString())
                                        .setVisitorFunction((s, classVisitor) ->
                                                new BaseAggregateRootRepositoryClassVisitor(classVisitor))
                                        .setCacheable(true)
                                        .build()
                        ));
    }

    @BuildStep
    void generateBaseAggregateRootRepository(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                             final BuildProducer<GeneratedBeanBuildItem> generatedBeanBuildItemBuildProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownSubclasses(AggregateRoot.class)
                .stream()
                .map(new ExtractAggregateRootTypesFromAggregateRoot())
                .forEach(aggregateRootTypes -> {
                    final Class<?> aggregateRootClass = aggregateRootTypes.aggregateRootClass();
                    final Class<?> aggregateIdClass = aggregateRootTypes.aggregateIdClass();
                    try (final ClassCreator beanClassCreator = ClassCreator.builder()
                            .classOutput(new GeneratedBeanGizmoAdaptor(generatedBeanBuildItemBuildProducer))
                            .className(aggregateRootTypes.aggregateRootClass().getName() + "RepositoryGenerated")
                            .signature(SignatureBuilder.forClass()
                                    .setSuperClass(
                                            Type.parameterizedType(
                                                    Type.classType(BaseAggregateRootRepository.class),
                                                    Type.classType(aggregateIdClass),
                                                    Type.classType(aggregateRootClass))))
                            .setFinal(false)
                            .build()) {
                        beanClassCreator.addAnnotation(Singleton.class);
                        beanClassCreator.addAnnotation(DefaultBean.class);

                        // constructor
                        final MethodCreator constructor = beanClassCreator.getMethodCreator(MethodDescriptor.INIT, void.class,
                                EventRepository.class, AggregateRootInstanceCreator.class, Instance.class);
                        constructor.setSignature(
                                SignatureBuilder.forMethod()
                                        .addParameterType(Type.parameterizedType(Type.classType(EventRepository.class), Type.classType(aggregateIdClass), Type.classType(aggregateRootClass)))
                                        .addParameterType(Type.classType(AggregateRootInstanceCreator.class))
                                        .addParameterType(Type.parameterizedType(Type.classType(Instance.class),
                                                Type.parameterizedType(Type.classType(BaseOnSavedEvent.class), Type.classType(aggregateIdClass), Type.classType(aggregateRootClass),
                                                        Type.wildcardTypeWithUpperBound(Type.parameterizedType(Type.classType(AggregateRootEventPayload.class), Type.classType(aggregateRootClass))))))
                                        .setReturnType(Type.voidType())
                                        .build());
                        constructor.setModifiers(Modifier.PUBLIC);
                        constructor.invokeSpecialMethod(MethodDescriptor.ofConstructor(BaseAggregateRootRepository.class,
                                        EventRepository.class, AggregateRootInstanceCreator.class, Instance.class),
                                constructor.getThis(), constructor.getMethodParam(0), constructor.getMethodParam(1), constructor.getMethodParam(2));
                        constructor.returnVoid();

                        // clazz
                        final MethodCreator clazzMethod = beanClassCreator.getMethodCreator(
                                BaseAggregateRootRepositoryClassVisitor.AGGREGATE_ROOT_CLASS_METHOD_NAMING, Class.class);
                        clazzMethod.setModifiers(Opcodes.ACC_PROTECTED);
                        clazzMethod.returnValue(clazzMethod.loadClass(aggregateRootClass));
                    }
                });
    }

    @BuildStep
    void enhanceBaseJdbcPostgresqlEventRepository(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                                  final BuildProducer<BytecodeTransformerBuildItem> bytecodeTransformerBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownSubclasses(BaseJdbcPostgresqlEventRepository.class)
                .forEach(classInfo ->
                        bytecodeTransformerBuildItemProducer.produce(
                                new BytecodeTransformerBuildItem.Builder()
                                        .setClassToTransform(classInfo.name().toString())
                                        .setVisitorFunction((s, classVisitor) ->
                                                new BaseJdbcPostgresqlEventRepositoryClassVisitor(classVisitor))
                                        .setCacheable(true)
                                        .build()
                        ));
    }

    @BuildStep
    void generateBaseJdbcPostgresqlEventRepository(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                                   final BuildProducer<GeneratedBeanBuildItem> generatedBeanBuildItemBuildProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownSubclasses(AggregateRoot.class)
                .stream()
                .map(new ExtractAggregateRootTypesFromAggregateRoot())
                .forEach(aggregateRootTypes -> {
                    final Class<?> aggregateRootClass = aggregateRootTypes.aggregateRootClass();
                    final Class<?> aggregateIdClass = aggregateRootTypes.aggregateIdClass();
                    try (final ClassCreator beanClassCreator = ClassCreator.builder()
                            .classOutput(new GeneratedBeanGizmoAdaptor(generatedBeanBuildItemBuildProducer))
                            .className(aggregateRootTypes.aggregateRootClass().getName() + "JdbcPostgresqlEventRepositoryGenerated")
                            .signature(SignatureBuilder.forClass()
                                    .setSuperClass(
                                            Type.parameterizedType(
                                                    Type.classType(BaseJdbcPostgresqlEventRepository.class),
                                                    Type.classType(aggregateIdClass),
                                                    Type.classType(aggregateRootClass)))
                            )
                            .setFinal(false)
                            .build()) {
                        beanClassCreator.addAnnotation(Singleton.class);
                        beanClassCreator.addAnnotation(DefaultBean.class);

                        // constructor
                        final MethodCreator constructor = beanClassCreator.getMethodCreator(MethodDescriptor.INIT, void.class,
                                AgroalDataSource.class, AggregateIdInstanceCreator.class, Instance.class);
                        constructor.setSignature(
                                SignatureBuilder.forMethod()
                                        .addParameterType(Type.classType(AgroalDataSource.class))
                                        .addParameterType(Type.classType(AggregateIdInstanceCreator.class))
                                        .addParameterType(Type.parameterizedType(Type.classType(Instance.class), Type.parameterizedType(Type.classType(AggregateRootEventPayloadSerde.class), Type.classType(aggregateRootClass), Type.wildcardTypeUnbounded())))
                                        .setReturnType(Type.voidType())
                                        .build());
                        constructor.setModifiers(Modifier.PUBLIC);
                        constructor.invokeSpecialMethod(MethodDescriptor.ofConstructor(BaseJdbcPostgresqlEventRepository.class, AgroalDataSource.class, AggregateIdInstanceCreator.class, Instance.class),
                                constructor.getThis(), constructor.getMethodParam(0), constructor.getMethodParam(1), constructor.getMethodParam(2));
                        constructor.returnVoid();

                        // clazz
                        final MethodCreator clazzMethod = beanClassCreator.getMethodCreator(
                                BaseJdbcPostgresqlEventRepositoryClassVisitor.AGGREGATE_ROOT_ID_CLASS_METHOD_NAMING, Class.class);
                        clazzMethod.setModifiers(Opcodes.ACC_PROTECTED);
                        clazzMethod.returnValue(clazzMethod.loadClass(aggregateIdClass));
                    }
                });
    }

    @BuildStep
    void enhanceOnSavedEvent(final ApplicationIndexBuildItem applicationIndexBuildItem,
                             final BuildProducer<BytecodeTransformerBuildItem> bytecodeTransformerBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownSubclasses(BaseOnSavedEvent.class)
                .forEach(classInfo ->
                        bytecodeTransformerBuildItemProducer.produce(
                                new BytecodeTransformerBuildItem.Builder()
                                        .setClassToTransform(classInfo.name().toString())
                                        .setVisitorFunction((s, classVisitor) ->
                                                new BaseOnSavedEventClassVisitor(classVisitor))
                                        .setCacheable(true)
                                        .build()
                        ));
    }

    @BuildStep
    void registerBaseOnSavedEventsAsSingletonBeans(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                                   final BuildProducer<AdditionalBeanBuildItem> additionalBeanBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownSubclasses(BaseOnSavedEvent.class)
                .forEach(classInfo ->
                        additionalBeanBuildItemProducer.produce(
                                new AdditionalBeanBuildItem.Builder()
                                        .addBeanClasses(classInfo.name().toString())
                                        .setUnremovable()
                                        .setDefaultScope(DotNames.SINGLETON)
                                        .build()
                        ));
    }

    @BuildStep
    void produceBeans(final BuildProducer<AdditionalBeanBuildItem> additionalBeanBuildItemProducer) {
        additionalBeanBuildItemProducer.produce(
                AdditionalBeanBuildItem.builder()
                        .setUnremovable()
                        .addBeanClasses(ReflectionAggregateIdInstanceCreator.class)
                        .build());
        additionalBeanBuildItemProducer.produce(
                AdditionalBeanBuildItem.builder()
                        .setUnremovable()
                        .addBeanClasses(ReflectionAggregateRootInstanceCreator.class)
                        .build());
        additionalBeanBuildItemProducer.produce(
                AdditionalBeanBuildItem.builder()
                        .setUnremovable()
                        .addBeanClasses(DefaultAggregateVersionIncrementer.class)
                        .build());
        additionalBeanBuildItemProducer.produce(
                AdditionalBeanBuildItem.builder()
                        .setUnremovable()
                        .addBeanClasses(DefaultCreatedAtProvider.class)
                        .build());
        additionalBeanBuildItemProducer.produce(
                AdditionalBeanBuildItem.builder()
                        .setUnremovable()
                        .addBeanClasses(PostgresqlInitializer.class)
                        .build());
    }

    @BuildStep
    void registerCustomExceptionMappers(final BuildProducer<CustomExceptionMapperBuildItem> customExceptionMapperBuildItemProducer) {
        customExceptionMapperBuildItemProducer.produce(new CustomExceptionMapperBuildItem(ExceptionsMapper.class.getName()));
    }

    @BuildStep
    void validateAggregateRootEventPayloadType(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                               final BuildProducer<ValidationErrorBuildItem> validationErrorBuildItemProducer) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(AggregateRootEventPayload.class)
                .forEach(classInfo -> {
                    try {
                        final Class<?> aggregateRootEventPayload = classLoader.loadClass(classInfo.name().toString());
                        assert AggregateRootEventPayload.class.isAssignableFrom(aggregateRootEventPayload);
                        if (!aggregateRootEventPayload.isRecord()) {
                            validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                                    new IllegalStateException(String.format("Domain Event '%s' must be a record", aggregateRootEventPayload.getName()))
                            ));
                        }
                    } catch (final ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @BuildStep
    void validateCommandRequestType(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                    final BuildProducer<ValidationErrorBuildItem> validationErrorBuildItemProducer) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(CommandRequest.class)
                .forEach(classInfo -> {
                    try {
                        final Class<?> commandRequest = classLoader.loadClass(classInfo.name().toString());
                        assert CommandRequest.class.isAssignableFrom(commandRequest);
                        if (!commandRequest.isRecord()) {
                            validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                                    new IllegalStateException(String.format("CommandRequest '%s' must be a record", commandRequest.getName()))
                            ));
                        }
                    } catch (final ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @BuildStep
    void validateAggregateCommandRequestType(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                             final BuildProducer<ValidationErrorBuildItem> validationErrorBuildItemProducer) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(AggregateCommandRequest.class)
                // TODO validate TypeAssignable
                .forEach(classInfo -> {
                    try {
                        final Class<?> aggregateCommandRequest = classLoader.loadClass(classInfo.name().toString());
                        assert AggregateCommandRequest.class.isAssignableFrom(aggregateCommandRequest);
                        if (!aggregateCommandRequest.isRecord()) {
                            validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                                    new IllegalStateException(String.format("AggregateCommandRequest '%s' must be a record", aggregateCommandRequest.getName()))
                            ));
                        }
                    } catch (final ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @BuildStep
    void validateQueryRequestType(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                  final BuildProducer<ValidationErrorBuildItem> validationErrorBuildItemProducer) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(QueryRequest.class)
                .forEach(classInfo -> {
                    try {
                        final Class<?> queryRequest = classLoader.loadClass(classInfo.name().toString());
                        assert QueryRequest.class.isAssignableFrom(queryRequest);
                        if (!queryRequest.isRecord()) {
                            validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                                    new IllegalStateException(String.format("QueryRequest '%s' must be a record", queryRequest.getName()))
                            ));
                        }
                    } catch (final ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @BuildStep
    void validateAggregateId(final ApplicationIndexBuildItem applicationIndexBuildItem,
                             final BuildProducer<ValidationErrorBuildItem> validationErrorBuildItemProducer) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(AggregateId.class)
                .forEach(classInfo -> {
                    try {
                        final Class<?> aggregateId = classLoader.loadClass(classInfo.name().toString());
                        assert AggregateId.class.isAssignableFrom(aggregateId);
                        if (!aggregateId.isRecord()) {
                            validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                                    new IllegalStateException(String.format("AggregateId '%s' must be a record", aggregateId.getName()))
                            ));
                        }
                    } catch (final ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @BuildStep
    void validateUseCaseExceptionNaming(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                        final BuildProducer<ValidationErrorBuildItem> validationErrorBuildItemProducer) {
        final Collection<ClassInfo> allBizMutationUseCaseImplementors = applicationIndexBuildItem.getIndex().getAllKnownImplementors(BizMutationUseCase.class);
        final Collection<ClassInfo> allBizQueryUseCaseImplementors = applicationIndexBuildItem.getIndex().getAllKnownImplementors(BizQueryUseCase.class);

        Stream.concat(allBizMutationUseCaseImplementors.stream(), allBizQueryUseCaseImplementors.stream())
                .map(new ExtractInterfaceParameterizedTypeFromClassInfo(BizMutationUseCase.class, BizQueryUseCase.class))
                .map(UseCaseExceptionNamingValidator::new)
                .filter(UseCaseExceptionNamingValidator::hasOnlyOneExceptionDefined)
                .filter(validator -> {
                    if (validator.isUseCaseWellDefined()) {
                        return validator.isInvalid();
                    } else {
                        LOGGER.warnf("Unable to validate exception naming for '%s' because the use case is bad named to check it. Use case naming will fail via 'validateBizMutationUseCaseNaming' or 'validateBizQueryUseCaseNaming' build steps.",
                                validator.implementorSimpleName());
                        return false;
                    }
                })
                .forEach(invalid ->
                        validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                                new IllegalStateException(String.format("'%s' execute method must define an exception called '%s' got '%s'",
                                        invalid.implementorName(),
                                        invalid.expectedExceptionNaming(), invalid.exceptionNaming()))
                        ))
                );
    }

    @BuildStep
    void validateUseCaseHasOnlyOneException(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                            final BuildProducer<ValidationErrorBuildItem> validationErrorBuildItemProducer) {
        final Collection<ClassInfo> allBizMutationUseCaseImplementors = applicationIndexBuildItem.getIndex().getAllKnownImplementors(BizMutationUseCase.class);
        final Collection<ClassInfo> allBizQueryUseCaseImplementors = applicationIndexBuildItem.getIndex().getAllKnownImplementors(BizQueryUseCase.class);

        Stream.concat(allBizMutationUseCaseImplementors.stream(), allBizQueryUseCaseImplementors.stream())
                .map(new ExtractInterfaceParameterizedTypeFromClassInfo(BizMutationUseCase.class, BizQueryUseCase.class))
                .map(UseCaseOnlyOneExceptionValidator::new)
                .filter(UseCaseOnlyOneExceptionValidator::isInvalid)
                .forEach(invalid ->
                        validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                                new IllegalStateException(String.format("'%s' execute method must define only one exception called '%s'", invalid.implementorNaming(), invalid.expectedExceptionNaming()))
                        ))
                );
    }

    @BuildStep
    void validateBizMutationUseCaseNaming(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                          final BuildProducer<ValidationErrorBuildItem> validationErrorBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(BizMutationUseCase.class)
                .stream()
                .map(classInfo -> new UseCaseNamingValidator(classInfo, BizMutationUseCase.class))
                .filter(UseCaseNamingValidator::isInvalid)
                .forEach(validator ->
                        validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                                new IllegalStateException(String.format("Bad naming for '%s', must end with '%s'",
                                        validator.implementorSimpleName(), validator.mustEndWith()))
                        ))
                );
    }

    @BuildStep
    void validateBizMutationUseCaseRequestCommand(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                                  final BuildProducer<ValidationErrorBuildItem> validationErrorBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(BizMutationUseCase.class)
                .stream()
                .map(BizMutationUseCaseRequestCommandValidator::new)
                .filter(validator -> {
                    if (validator.canValidate()) {
                        return validator.isInvalid();
                    } else {
                        LOGGER.warnf("Unable to validate command request naming for '%s' because the use case is bad named to validate it. Use case naming will fail via 'validateBizMutationUseCaseNaming' build step.",
                                validator.useCaseNaming());
                        return false;
                    }
                })
                .forEach(validator ->
                        validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                                new IllegalStateException(String.format("Bad naming for command request '%s', expected '%s'",
                                        validator.commandRequestCurrentNaming(), validator.commandRequestExpectedNaming()))
                        )));
    }

    @BuildStep
    void validateBizQueryUseCaseProjectionType(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                               final BuildProducer<ValidationErrorBuildItem> validationErrorBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(BizQueryUseCase.class)
                .stream()
                .map(new ExtractInterfaceParameterizedTypeFromClassInfo(BizQueryUseCase.class))
                .map(BizQueryUseCaseProjectionTypeValidator::new)// FCK je devrais avoir un autre nommage qui prennent les classes assignable en entrée ...
                .filter(BizQueryUseCaseProjectionTypeValidator::isInvalid)
                .forEach(invalid ->
                        validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                                new IllegalStateException(String.format("BizQueryUseCase '%s' projection must implement VersionedProjection or be a ListOfProjection", invalid.implementorNaming()))
                        ))
                );
    }

    @BuildStep
    void validateBizQueryUseCaseNaming(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                       final BuildProducer<ValidationErrorBuildItem> validationErrorBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(BizQueryUseCase.class)
                .stream()
                .map(implementor -> new UseCaseNamingValidator(implementor, BizQueryUseCase.class))
                .filter(UseCaseNamingValidator::isInvalid)
                .forEach(validator ->
                        validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                                new IllegalStateException(String.format("Bad naming for '%s', must end with '%s'",
                                        validator.implementorSimpleName(), validator.mustEndWith()))
                        ))
                );
    }

    @BuildStep
    void validateBizQueryUseCaseRequestCommand(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                               final BuildProducer<ValidationErrorBuildItem> validationErrorBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(BizQueryUseCase.class)
                .stream()
                .map(BizQueryUseCaseRequestCommandValidator::new)
                .filter(validator -> {
                    if (validator.canValidate()) {
                        return validator.isInvalid();
                    } else {
                        LOGGER.warnf("Unable to validate query request naming for '%s' because the use case is bad named to validate it. Use case naming will fail via 'validateBizQueryUseCaseNaming' build step.",
                                validator.useCaseNaming());
                        return false;
                    }
                })
                .forEach(validator ->
                        validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                                new IllegalStateException(String.format("Bad naming for query request '%s', expected '%s'",
                                        validator.queryRequestCurrentNaming(), validator.queryRequestExpectedNaming()))
                        )));
    }

    @BuildStep
    void registerBizMutationUseCasesAsSingletonBean(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                                    final BuildProducer<AdditionalBeanBuildItem> additionalBeanBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(BizMutationUseCase.class)
                .forEach(implementor ->
                        additionalBeanBuildItemProducer.produce(
                                new AdditionalBeanBuildItem.Builder()
                                        .addBeanClasses(implementor.name().toString())
                                        .setUnremovable()
                                        .setDefaultScope(DotNames.SINGLETON)
                                        .build()
                        )
                );
    }

    @BuildStep
    void enhanceBizMutationUseCase(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                   final BuildProducer<BytecodeTransformerBuildItem> bytecodeTransformerBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(BizMutationUseCase.class)
                .forEach(implementor ->
                        bytecodeTransformerBuildItemProducer.produce(
                                new BytecodeTransformerBuildItem.Builder()
                                        .setClassToTransform(implementor.name().toString())
                                        .setVisitorFunction((s, classVisitor) ->
                                                new BizMutationUseCaseClassVisitor(classVisitor))
                                        .setCacheable(true)
                                        .build()
                        ));
    }

    @BuildStep
    void registerBizQueryUseCasesAsSingletonBean(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                                 final BuildProducer<AdditionalBeanBuildItem> additionalBeanBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(BizQueryUseCase.class)
                .forEach(implementor ->
                        additionalBeanBuildItemProducer.produce(
                                new AdditionalBeanBuildItem.Builder()
                                        .addBeanClasses(implementor.name().toString())
                                        .setUnremovable()
                                        .setDefaultScope(DotNames.SINGLETON)
                                        .build()
                        )
                );
    }

    @BuildStep
    void enhanceBizQueryUseCase(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                final BuildProducer<BytecodeTransformerBuildItem> bytecodeTransformerBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(BizQueryUseCase.class)
                .forEach(implementor ->
                        bytecodeTransformerBuildItemProducer.produce(
                                new BytecodeTransformerBuildItem.Builder()
                                        .setClassToTransform(implementor.name().toString())
                                        .setVisitorFunction((s, classVisitor) ->
                                                new BizQueryUseCaseClassVisitor(classVisitor))
                                        .setCacheable(true)
                                        .build()
                        ));
    }

    // TODO discover endpoint and check that only use cases are injected
    // TODO check that all beans use constructor injection by scanning @Inject fields and disallow them
    // TODO check presence of openapi annotations
}
