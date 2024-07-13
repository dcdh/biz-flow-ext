package io.bizflowframework.biz.flow.ext.deployment;

import io.quarkus.bootstrap.classloading.QuarkusClassLoader;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.ParameterizedType;

import java.util.Objects;
import java.util.function.Function;

public class ExtractAggregateRootEventPayloadKeyFromPayload implements Function<ParameterizedType, AggregateRootEventPayloadKey> {

    private final ClassInfo classInfo;

    public ExtractAggregateRootEventPayloadKeyFromPayload(final ClassInfo classInfo) {
        this.classInfo = Objects.requireNonNull(classInfo);
    }

    @Override
    public AggregateRootEventPayloadKey apply(final ParameterizedType parameterizedType) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            final Class<?> aggregateRootClass = classLoader.loadClass(parameterizedType.arguments().getFirst().name().toString());
            final Class<?> eventPayloadClassName = classLoader.loadClass(classInfo.name().toString());
            return new AggregateRootEventPayloadKey(aggregateRootClass, eventPayloadClassName);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
