package io.bizflowframework.biz.flow.ext.deployment;

import org.jboss.jandex.ParameterizedType;

import java.util.List;
import java.util.function.Function;

public record ExtractAggregateRootEventPayloadSerdeKey() implements Function<ParameterizedType, AggregateRootEventPayloadSerdeKey> {

    @Override
    public AggregateRootEventPayloadSerdeKey apply(final ParameterizedType type) {
        final List<org.jboss.jandex.Type> arguments = type.arguments();
        assert arguments.size() == 2;
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            final Class<?> aggregateRootClass = classLoader.loadClass(arguments.getFirst().name().toString());
            final Class<?> aggregateRootEventPayloadClass = classLoader.loadClass(arguments.get(1).name().toString());
            return new AggregateRootEventPayloadSerdeKey(aggregateRootClass, aggregateRootEventPayloadClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
