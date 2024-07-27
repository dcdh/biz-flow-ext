package io.bizflowframework.biz.flow.ext.deployment;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.EventHandler;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ApplicationIndexBuildItem;
import io.quarkus.deployment.builditem.BytecodeTransformerBuildItem;

public class EventHandlerProcessor {
    @BuildStep
    void enhanceEventHandler(final ApplicationIndexBuildItem applicationIndexBuildItem,
                             final BuildProducer<BytecodeTransformerBuildItem> bytecodeTransformerBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownSubclasses(EventHandler.class)
                .forEach(classInfo ->
                        bytecodeTransformerBuildItemProducer.produce(
                                new BytecodeTransformerBuildItem.Builder()
                                        .setClassToTransform(classInfo.name().toString())
                                        .setVisitorFunction((s, classVisitor) ->
                                                new EventHandlerClassVisitor(classVisitor))
                                        .setCacheable(true)
                                        .build()
                        ));
    }

    @BuildStep
    void registerEventsHandlersAsSingletonBeans(final ApplicationIndexBuildItem applicationIndexBuildItem,
                                                final BuildProducer<AdditionalBeanBuildItem> additionalBeanBuildItemProducer) {
        applicationIndexBuildItem.getIndex()
                .getAllKnownSubclasses(EventHandler.class)
                .forEach(classInfo ->
                        additionalBeanBuildItemProducer.produce(
                                new AdditionalBeanBuildItem.Builder()
                                        .addBeanClasses(classInfo.name().toString())
                                        .setUnremovable()
                                        .setDefaultScope(DotNames.SINGLETON)
                                        .build()
                        ));
    }
}
