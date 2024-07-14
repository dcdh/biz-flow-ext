package io.bizflowframework.biz.flow.ext.deployment;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.ParameterizedType;

import java.util.function.Function;

public record ExtractAggregateRootEventPayloadDescriptor() implements Function<ExtractedInterfaceParameterizedType, AggregateRootEventPayloadDescriptor> {

    @Override
    public AggregateRootEventPayloadDescriptor apply(final ExtractedInterfaceParameterizedType extract) {
        final ClassInfo implementor = extract.implementor();
        final ParameterizedType parameterizedType = extract.parameterizedType();
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            final Class<?> aggregateRootClass = classLoader.loadClass(parameterizedType.arguments().getFirst().name().toString());
            final Class<?> eventPayloadClassName = classLoader.loadClass(implementor.name().toString());
            return new AggregateRootEventPayloadDescriptor(aggregateRootClass, eventPayloadClassName);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
