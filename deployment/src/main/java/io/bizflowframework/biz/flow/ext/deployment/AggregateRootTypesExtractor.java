package io.bizflowframework.biz.flow.ext.deployment;

import org.jboss.jandex.ClassInfo;

import java.util.function.Function;

public record AggregateRootTypesExtractor() implements Function<ClassInfo, AggregateRootTypes> {
    @Override
    public AggregateRootTypes apply(final ClassInfo classInfo) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            final Class<?> aggregateRootClass = classLoader.loadClass(classInfo.name().toString());
            final Class<?> aggregateIdClass = classLoader.loadClass(classInfo.superClassType().asParameterizedType().arguments().getFirst().toString());
            return new AggregateRootTypes(aggregateRootClass, aggregateIdClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
