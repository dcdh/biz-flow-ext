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
import io.bizflowframework.biz.flow.ext.runtime.usecase.*;
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
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.ParameterizedType;
import org.jboss.logging.Logger;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

class BizFlowExtProcessor {
    private static final Logger LOGGER = Logger.getLogger(BizFlowExtProcessor.class);

    private static final String FEATURE = "biz-flow-ext";

    private static final String BIZ_MUTATION_USE_CASE_SIMPLE_NAME = BizMutationUseCase.class.getSimpleName();

    private static final String BIZ_QUERY_USE_CASE_SIMPLE_NAME = BizQueryUseCase.class.getSimpleName();

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void enhanceAggregateRootEventPayloadSerde(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                               final BuildProducer<BytecodeTransformerBuildItem> bytecodeTransformerBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(AggregateRootEventPayloadSerde.class)
                .forEach(classInfo ->
                        bytecodeTransformerBuildItemProducer.produce(
                                new BytecodeTransformerBuildItem.Builder()
                                        .setClassToTransform(classInfo.name().toString())
                                        .setVisitorFunction((s, classVisitor) ->
                                                new AggregateRootEventPayloadSerdeClassVisitor(classVisitor))
                                        .setCacheable(true)
                                        .build()
                        ));
    }

    @BuildStep
    void enhanceBaseAggregateRootRepository(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                            final BuildProducer<BytecodeTransformerBuildItem> bytecodeTransformerBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownSubclasses(BaseAggregateRootRepository.class)
                .forEach(classInfo ->
                        bytecodeTransformerBuildItemProducer.produce(
                                new BytecodeTransformerBuildItem.Builder()
                                        .setClassToTransform(classInfo.name().toString())
                                        .setVisitorFunction((s, classVisitor) ->
                                                new BaseAggregateRootRepositoryClassVisitor(classVisitor))
                                        .setCacheable(true)
                                        .build()
                        ));
    }

    @BuildStep
    void generateBaseAggregateRootRepository(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                             final BuildProducer<GeneratedBeanBuildItem> generatedBeanBuildItemBuildProducer) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        applicationIndexBuildItem.getIndex()
                .getAllKnownSubclasses(AggregateRoot.class)
                .forEach(classInfo -> {
                    try {
                        final Class<?> aggregateRootClass = classLoader.loadClass(classInfo.name().toString());
                        assert AggregateRoot.class.isAssignableFrom(aggregateRootClass);
                        final Class<?> aggregateIdClass = classLoader.loadClass(classInfo.superClassType().asParameterizedType().arguments().getFirst().toString());
                        assert AggregateId.class.isAssignableFrom(aggregateIdClass);
                        try (final ClassCreator beanClassCreator = ClassCreator.builder()
                                .classOutput(new GeneratedBeanGizmoAdaptor(generatedBeanBuildItemBuildProducer))
                                .className(classInfo.name() + "RepositoryGenerated")
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
                    } catch (final ClassNotFoundException classNotFoundException) {
                        throw new IllegalStateException("Should not be here");
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
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        applicationIndexBuildItem.getIndex()
                .getAllKnownSubclasses(AggregateRoot.class)
                .forEach(classInfo -> {
                    try {
                        final Class<?> aggregateRootClass = classLoader.loadClass(classInfo.name().toString());
                        assert AggregateRoot.class.isAssignableFrom(aggregateRootClass);
                        final Class<?> aggregateIdClass = classLoader.loadClass(classInfo.superClassType().asParameterizedType().arguments().getFirst().toString());
                        assert AggregateId.class.isAssignableFrom(aggregateIdClass);
                        try (final ClassCreator beanClassCreator = ClassCreator.builder()
                                .classOutput(new GeneratedBeanGizmoAdaptor(generatedBeanBuildItemBuildProducer))
                                .className(classInfo.name() + "JdbcPostgresqlEventRepositoryGenerated")
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
                    } catch (final ClassNotFoundException classNotFoundException) {
                        throw new IllegalStateException("Should not be here");
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

        Stream.concat(allBizMutationUseCaseImplementors.stream(),
                        allBizQueryUseCaseImplementors.stream())
                .forEach(classInfo ->
                        new FindInterfaceFromClassInfo(BizMutationUseCase.class, BizQueryUseCase.class)
                                .andThen(interfacePosition -> {
                                    final List<org.jboss.jandex.Type> arguments = ((ParameterizedType) classInfo.interfaceTypes().get(interfacePosition)).arguments();
                                    assert arguments.size() == 3;
                                    final MethodInfo execute = classInfo.method("execute", arguments.get(1));
                                    final String expectedExceptionNaming = classInfo.simpleName() + "Exception";
                                    if (execute.exceptions().size() > 1) {
                                        validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                                                new IllegalStateException(String.format("'%s' execute method must define only one exception called '%s'", classInfo.name(), expectedExceptionNaming))
                                        ));
                                    } else {
                                        if (classInfo.simpleName().endsWith(BIZ_MUTATION_USE_CASE_SIMPLE_NAME)
                                                || classInfo.simpleName().endsWith(BIZ_QUERY_USE_CASE_SIMPLE_NAME)) {
                                            new ExtractClassNaming()
                                                    .andThen(exceptionNaming -> {
                                                        if (!expectedExceptionNaming.equals(exceptionNaming)) {
                                                            validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                                                                    new IllegalStateException(String.format("'%s' execute method must define an exception called '%s' got '%s'", classInfo.name(),
                                                                            expectedExceptionNaming, exceptionNaming))
                                                            ));
                                                        }
                                                        return null;
                                                    })
                                                    .apply(execute.exceptions().getFirst());
                                        } else {
                                            LOGGER.warnf("Unable to validate exception naming for '%s' because the use case is bad named to check it. Use case naming will fail via 'validateBizMutationUseCaseNaming' or 'validateBizQueryUseCaseNaming' build steps.",
                                                    classInfo.simpleName());
                                        }
                                    }
                                    return null;
                                })
                                .apply(classInfo)
                );
    }

    @BuildStep
    void validateBizMutationUseCaseNaming(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                          final BuildProducer<ValidationErrorBuildItem> validationErrorBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(BizMutationUseCase.class)
                .stream()
                .map(ClassInfo::simpleName)
                .filter(bizQueryUseCaseSimpleName -> !bizQueryUseCaseSimpleName.endsWith(BIZ_MUTATION_USE_CASE_SIMPLE_NAME))
                .forEach(badNamingBizQueryUseCaseSimpleName -> {
                    validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                            new IllegalStateException(String.format("Bad naming for '%s', must end with '%s'",
                                    badNamingBizQueryUseCaseSimpleName, BIZ_MUTATION_USE_CASE_SIMPLE_NAME))
                    ));
                });
    }

    @BuildStep
    void validateBizMutationUseCaseRequestCommand(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                                  final BuildProducer<ValidationErrorBuildItem> validationErrorBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(BizMutationUseCase.class)
                .forEach(classInfo ->
                        new FindInterfaceFromClassInfo(BizMutationUseCase.class)
                                .andThen(interfacePosition -> {
                                    final List<org.jboss.jandex.Type> arguments = ((ParameterizedType) classInfo.interfaceTypes().get(interfacePosition)).arguments();
                                    assert arguments.size() == 3;
                                    final int indexOfBizMutationUseCase = classInfo.simpleName().indexOf(BIZ_MUTATION_USE_CASE_SIMPLE_NAME);
                                    if (indexOfBizMutationUseCase > -1) {
                                        final String expectedNaming = classInfo.simpleName().substring(0, indexOfBizMutationUseCase) + "CommandRequest";
                                        new ExtractClassNaming()
                                                .andThen(currentNaming -> {
                                                    if (!expectedNaming.equals(currentNaming)) {
                                                        validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                                                                new IllegalStateException(String.format("Bad naming for command request '%s', expected '%s'",
                                                                        currentNaming, expectedNaming))
                                                        ));
                                                    }
                                                    return null;
                                                })
                                                .apply(arguments.get(1));
                                    } else {
                                        LOGGER.warnf("Unable to validate command request naming for '%s' because the use case is bad named to validate it. Use case naming will fail via 'validateBizMutationUseCaseNaming' build step.",
                                                classInfo.simpleName());
                                    }
                                    return null;
                                })
                                .apply(classInfo)
                );
    }

    @BuildStep
    void validateBizQueryProjectionType(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                        final BuildProducer<ValidationErrorBuildItem> validationErrorBuildItemProducer) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(BizQueryUseCase.class)
                .forEach(classInfo ->
                        new FindInterfaceFromClassInfo(BizQueryUseCase.class)
                                .andThen(interfacePosition -> {
                                    try {
                                        final List<org.jboss.jandex.Type> arguments = ((ParameterizedType) classInfo.interfaceTypes().get(interfacePosition)).arguments();
                                        assert arguments.size() == 3;
                                        final org.jboss.jandex.Type projectionType = arguments.getFirst();
                                        final Class<?> projectionClass = classLoader.loadClass(projectionType.name().toString());

                                        if (!VersionedProjection.class.isAssignableFrom(projectionClass)
                                                && !ListOfProjection.class.isAssignableFrom(projectionClass)) {
                                            validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                                                    new IllegalStateException(String.format("BizQueryUseCase '%s' projection must implement VersionedProjection or be a ListOfProjection", classInfo.name()))
                                            ));
                                        }
                                        return null;
                                    } catch (final ClassNotFoundException classNotFoundException) {
                                        throw new IllegalStateException("Should not be here");
                                    }
                                })
                                .apply(classInfo)
                );
    }

    @BuildStep
    void validateBizQueryUseCaseNaming(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                       final BuildProducer<ValidationErrorBuildItem> validationErrorBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(BizQueryUseCase.class)
                .stream()
                .map(ClassInfo::simpleName)
                .filter(bizQueryUseCaseSimpleName -> !bizQueryUseCaseSimpleName.endsWith(BIZ_QUERY_USE_CASE_SIMPLE_NAME))
                .forEach(badNamingBizQueryUseCaseSimpleName -> {
                    validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                            new IllegalStateException(String.format("Bad naming for '%s', must end with '%s'",
                                    badNamingBizQueryUseCaseSimpleName, BIZ_QUERY_USE_CASE_SIMPLE_NAME))
                    ));
                });
    }

    @BuildStep
    void validateBizQueryUseCaseRequestCommand(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                               final BuildProducer<ValidationErrorBuildItem> validationErrorBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(BizQueryUseCase.class)
                .forEach(classInfo ->
                        new FindInterfaceFromClassInfo(BizQueryUseCase.class)
                                .andThen(interfacePosition -> {
                                    final List<org.jboss.jandex.Type> arguments = ((ParameterizedType) classInfo.interfaceTypes().get(interfacePosition)).arguments();
                                    assert arguments.size() == 3;
                                    final int indexOfBizQueryUseCase = classInfo.simpleName().indexOf(BIZ_QUERY_USE_CASE_SIMPLE_NAME);
                                    if (indexOfBizQueryUseCase > -1) {
                                        final String expectedNaming = classInfo.simpleName().substring(0, indexOfBizQueryUseCase) + "QueryRequest";
                                        new ExtractClassNaming()
                                                .andThen(currentNaming -> {
                                                    if (!expectedNaming.equals(currentNaming)) {
                                                        validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                                                                new IllegalStateException(String.format("Bad naming for query request '%s', expected '%s'",
                                                                        currentNaming, expectedNaming))
                                                        ));
                                                    }
                                                    return null;
                                                })
                                                .apply(arguments.get(1));
                                    } else {
                                        LOGGER.warnf("Unable to validate query request naming for '%s' because the use case is bad named to validate it. Use case naming will fail via 'validateBizQueryUseCaseNaming' build step.",
                                                classInfo.simpleName());
                                    }
                                    return null;
                                })
                                .apply(classInfo)
                );
    }

    @BuildStep
    void registerBizMutationUseCasesAsSingletonBean(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                                    final BuildProducer<AdditionalBeanBuildItem> additionalBeanBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(BizMutationUseCase.class)
                .forEach(classInfo ->
                        additionalBeanBuildItemProducer.produce(
                                new AdditionalBeanBuildItem.Builder()
                                        .addBeanClasses(classInfo.name().toString())
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
                .forEach(classInfo ->
                        bytecodeTransformerBuildItemProducer.produce(
                                new BytecodeTransformerBuildItem.Builder()
                                        .setClassToTransform(classInfo.name().toString())
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
                .forEach(classInfo ->
                        additionalBeanBuildItemProducer.produce(
                                new AdditionalBeanBuildItem.Builder()
                                        .addBeanClasses(classInfo.name().toString())
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
                .forEach(classInfo ->
                        bytecodeTransformerBuildItemProducer.produce(
                                new BytecodeTransformerBuildItem.Builder()
                                        .setClassToTransform(classInfo.name().toString())
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
