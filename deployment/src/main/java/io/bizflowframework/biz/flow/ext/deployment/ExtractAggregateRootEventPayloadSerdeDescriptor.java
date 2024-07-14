package io.bizflowframework.biz.flow.ext.deployment;

import org.jboss.jandex.ParameterizedType;

import java.util.List;
import java.util.function.Function;

public record ExtractAggregateRootEventPayloadSerdeDescriptor()
        implements Function<ExtractedInterfaceParameterizedType, AggregateRootEventPayloadSerdeDescriptor> {

    @Override
    public AggregateRootEventPayloadSerdeDescriptor apply(final ExtractedInterfaceParameterizedType extracted) {
        final ParameterizedType parameterizedType = extracted.parameterizedType();
        final List<org.jboss.jandex.Type> arguments = parameterizedType.arguments();
        assert arguments.size() == 2;
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            final Class<?> aggregateRootEventPayloadSerde = classLoader.loadClass(extracted.implementor().name().toString());
            final Class<?> aggregateRootClass = classLoader.loadClass(arguments.getFirst().name().toString());
            final Class<?> aggregateRootEventPayloadClass = classLoader.loadClass(arguments.get(1).name().toString());
            return new AggregateRootEventPayloadSerdeDescriptor(aggregateRootEventPayloadSerde, aggregateRootClass,
                    aggregateRootEventPayloadClass);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
