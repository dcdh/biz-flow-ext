package io.bizflowframework.biz.flow.ext.deployment;

import org.jboss.jandex.ClassInfo;

import java.util.Objects;
import java.util.function.Predicate;

public record IsImplementorNameDoesEndsWithClazzByName(Class<?> clazz) implements Predicate<ClassInfo> {

    public IsImplementorNameDoesEndsWithClazzByName {
        Objects.requireNonNull(clazz);
    }

    @Override
    public boolean test(final ClassInfo implementor) {
        return implementor.simpleName().endsWith(clazz.getSimpleName());
    }
}
