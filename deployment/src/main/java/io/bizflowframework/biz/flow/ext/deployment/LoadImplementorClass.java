package io.bizflowframework.biz.flow.ext.deployment;

import org.jboss.jandex.ClassInfo;

import java.util.Objects;
import java.util.function.Function;

public record LoadImplementorClass(Class<?> expectedAssignableClass) implements Function<ClassInfo, Class<?>> {

    public LoadImplementorClass {
        Objects.requireNonNull(expectedAssignableClass);
    }

    @Override
    public Class<?> apply(final ClassInfo implementor) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            final Class<?> implementorClass = classLoader.loadClass(implementor.name().toString());
            assert expectedAssignableClass.isAssignableFrom(implementorClass);
            return implementorClass;
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException("Should not be here", e);
        }
    }

}
