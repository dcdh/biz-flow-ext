package io.bizflowframework.biz.flow.ext.deployment;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.ParameterizedType;

import java.util.function.Function;

public record ExtractAggregateRootEventPayloadKeyFromPayload() implements Function<ExtractInterfaceParameterizedTypeResult, AggregateRootEventPayloadKey> {

    @Override
    public AggregateRootEventPayloadKey apply(final ExtractInterfaceParameterizedTypeResult extractInterfaceParameterizedTypeResult) {
        final ClassInfo implementor = extractInterfaceParameterizedTypeResult.implementor();
        final ParameterizedType parameterizedType = extractInterfaceParameterizedTypeResult.parameterizedType();
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            final Class<?> aggregateRootClass = classLoader.loadClass(parameterizedType.arguments().getFirst().name().toString());
            final Class<?> eventPayloadClassName = classLoader.loadClass(implementor.name().toString());
            return new AggregateRootEventPayloadKey(aggregateRootClass, eventPayloadClassName);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
