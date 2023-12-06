package io.bizflowframework.biz.flow.ext.deployment;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.DefaultCreatedAtProvider;
import io.bizflowframework.biz.flow.ext.runtime.api.ExceptionsMapper;
import io.bizflowframework.biz.flow.ext.runtime.command.Command;
import io.bizflowframework.biz.flow.ext.runtime.creational.ReflectionAggregateIdInstanceCreator;
import io.bizflowframework.biz.flow.ext.runtime.creational.ReflectionAggregateRootInstanceCreator;
import io.bizflowframework.biz.flow.ext.runtime.event.AggregateRootEventPayload;
import io.bizflowframework.biz.flow.ext.runtime.event.PostgresqlInitializer;
import io.bizflowframework.biz.flow.ext.runtime.incrementer.DefaultAggregateVersionIncrementer;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.ValidationPhaseBuildItem.ValidationErrorBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ApplicationIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.resteasy.reactive.spi.CustomExceptionMapperBuildItem;
import org.jboss.jandex.DotName;

class BizFlowExtProcessor {

    private static final String FEATURE = "biz-flow-ext";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
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
}
