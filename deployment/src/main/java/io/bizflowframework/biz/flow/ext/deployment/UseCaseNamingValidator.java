package io.bizflowframework.biz.flow.ext.deployment;

import io.bizflowframework.biz.flow.ext.runtime.usecase.UseCase;
import org.jboss.jandex.ClassInfo;

import java.util.Objects;

public record UseCaseNamingValidator(ClassInfo implementor, Class<?> useCaseClass) {

    public UseCaseNamingValidator {
        Objects.requireNonNull(implementor);
        Objects.requireNonNull(useCaseClass);
        try {
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            assert UseCase.class.isAssignableFrom(classLoader.loadClass(implementor.name().toString()));
            assert UseCase.class.isAssignableFrom(useCaseClass);
        } catch (final ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    public boolean isInvalid() {
        return !implementor.simpleName().endsWith(useCaseClass.getSimpleName());
    }

    public String implementorSimpleName() {
        return implementor.simpleName();
    }

    public String mustEndWith() {
        return useCaseClass.getSimpleName();
    }
}
