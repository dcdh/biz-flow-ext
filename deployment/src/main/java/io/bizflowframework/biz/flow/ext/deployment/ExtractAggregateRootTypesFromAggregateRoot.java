package io.bizflowframework.biz.flow.ext.deployment;

import org.jboss.jandex.ClassInfo;

import java.util.function.Function;

public record ExtractAggregateRootTypesFromAggregateRoot() implements Function<ClassInfo, AggregateRootTypes> {

    @Override
    public AggregateRootTypes apply(final ClassInfo implementor) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            final Class<?> aggregateRootClass = classLoader.loadClass(implementor.name().toString());
            final Class<?> aggregateIdClass = classLoader.loadClass(implementor.superClassType().asParameterizedType().arguments().getFirst().toString());
            return new AggregateRootTypes(aggregateRootClass, aggregateIdClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
