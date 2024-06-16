package io.bizflowframework.biz.flow.ext.deployment;

import io.agroal.api.AgroalDataSource;
import io.bizflowframework.biz.flow.ext.runtime.*;
import io.bizflowframework.biz.flow.ext.runtime.api.ExceptionsMapper;
import io.bizflowframework.biz.flow.ext.runtime.command.Command;
import io.bizflowframework.biz.flow.ext.runtime.command.CommandHandler;
import io.bizflowframework.biz.flow.ext.runtime.creational.AggregateIdInstanceCreator;
import io.bizflowframework.biz.flow.ext.runtime.creational.AggregateRootInstanceCreator;
import io.bizflowframework.biz.flow.ext.runtime.creational.ReflectionAggregateIdInstanceCreator;
import io.bizflowframework.biz.flow.ext.runtime.creational.ReflectionAggregateRootInstanceCreator;
import io.bizflowframework.biz.flow.ext.runtime.event.AggregateRootEventPayload;
import io.bizflowframework.biz.flow.ext.runtime.event.BaseJdbcPostgresqlEventRepository;
import io.bizflowframework.biz.flow.ext.runtime.event.EventRepository;
import io.bizflowframework.biz.flow.ext.runtime.event.PostgresqlInitializer;
import io.bizflowframework.biz.flow.ext.runtime.incrementer.DefaultAggregateVersionIncrementer;
import io.bizflowframework.biz.flow.ext.runtime.serde.AggregateRootEventPayloadSerde;
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.GeneratedBeanBuildItem;
import io.quarkus.arc.deployment.GeneratedBeanGizmoAdaptor;
import io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ApplicationIndexBuildItem;
import io.quarkus.deployment.builditem.BytecodeTransformerBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.gizmo.*;
import io.quarkus.resteasy.reactive.spi.CustomExceptionMapperBuildItem;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Singleton;
import org.jboss.jandex.DotName;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Modifier;

class BizFlowExtProcessor {

    private static final String FEATURE = "biz-flow-ext";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void enhanceAggregateRootEventPayloadSerde(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                               final BuildProducer<BytecodeTransformerBuildItem> bytecodeTransformerBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(DotName.createSimple(AggregateRootEventPayloadSerde.class))
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
                .getAllKnownSubclasses(DotName.createSimple(BaseAggregateRootRepository.class))
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
                .getAllKnownSubclasses(DotName.createSimple(AggregateRoot.class))
                .forEach(classInfo -> {
                    try {
                        final Class<?> aggregateRootClass = classLoader.loadClass(classInfo.name().toString());
                        final Class<?> aggregateIdClass = classLoader.loadClass(classInfo.superClassType().asParameterizedType().arguments().get(0).toString());
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
                .getAllKnownSubclasses(DotName.createSimple(BaseJdbcPostgresqlEventRepository.class))
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
                .getAllKnownSubclasses(DotName.createSimple(AggregateRoot.class))
                .forEach(classInfo -> {
                    try {
                        final Class<?> aggregateRootClass = classLoader.loadClass(classInfo.name().toString());
                        final Class<?> aggregateIdClass = classLoader.loadClass(classInfo.superClassType().asParameterizedType().arguments().get(0).toString());
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
                .getAllKnownSubclasses(DotName.createSimple(BaseOnSavedEvent.class))
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
                .getAllKnownImplementors(DotName.createSimple(AggregateRootEventPayload.class))
                .forEach(classInfo -> {
                    try {
                        final Class<?> aggregateRootEventPayload = classLoader.loadClass(classInfo.name().toString());
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
    void validateCommandType(final ApplicationIndexBuildItem applicationIndexBuildItem,
                             final BuildProducer<ValidationErrorBuildItem> validationErrorBuildItemProducer) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(DotName.createSimple(Command.class))
                .forEach(classInfo -> {
                    try {
                        final Class<?> command = classLoader.loadClass(classInfo.name().toString());
                        if (!command.isRecord()) {
                            validationErrorBuildItemProducer.produce(new ValidationErrorBuildItem(
                                    new IllegalStateException(String.format("Command '%s' must be a record", command.getName()))
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
                .getAllKnownImplementors(DotName.createSimple(AggregateId.class))
                .forEach(classInfo -> {
                    try {
                        final Class<?> aggregateId = classLoader.loadClass(classInfo.name().toString());
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
    void registerCommandHandlersAsSingletonBean(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                                final BuildProducer<BytecodeTransformerBuildItem> bytecodeTransformerBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownImplementors(DotName.createSimple(CommandHandler.class))
                .forEach(classInfo -> {
System.out.print("ICI DAMIEN mais lol 3!!! " + classInfo.toString());
                    bytecodeTransformerBuildItemProducer.produce(
                            new BytecodeTransformerBuildItem.Builder()
                                    .setClassToTransform(classInfo.name().toString())
                                    .setVisitorFunction((s, classVisitor) ->
                                            new AddSingletonAnnotationClassVisitor(classVisitor))
                                    .setCacheable(true)
                                    .build()
                    );
                });
    }
}
