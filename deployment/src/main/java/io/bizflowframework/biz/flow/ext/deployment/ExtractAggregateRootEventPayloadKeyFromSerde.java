package io.bizflowframework.biz.flow.ext.deployment;

import org.jboss.jandex.ParameterizedType;

import java.util.List;
import java.util.function.Function;

public record ExtractAggregateRootEventPayloadKeyFromSerde() implements Function<ExtractInterfaceParameterizedTypeResult, AggregateRootEventPayloadKey> {

    @Override
    public AggregateRootEventPayloadKey apply(final ExtractInterfaceParameterizedTypeResult extractInterfaceParameterizedTypeResult) {
        final ParameterizedType parameterizedType = extractInterfaceParameterizedTypeResult.parameterizedType();
        final List<org.jboss.jandex.Type> arguments = parameterizedType.arguments();
        assert arguments.size() == 2;
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            final Class<?> aggregateRootClass = classLoader.loadClass(arguments.getFirst().name().toString());
            final Class<?> aggregateRootEventPayloadClass = classLoader.loadClass(arguments.get(1).name().toString());
            return new AggregateRootEventPayloadKey(aggregateRootClass, aggregateRootEventPayloadClass);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
