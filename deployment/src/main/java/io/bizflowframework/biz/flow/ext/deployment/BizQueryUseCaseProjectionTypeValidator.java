package io.bizflowframework.biz.flow.ext.deployment;

import io.bizflowframework.biz.flow.ext.runtime.usecase.ListOfProjection;
import io.bizflowframework.biz.flow.ext.runtime.usecase.VersionedProjection;
import org.jboss.jandex.Type;

import java.util.List;
import java.util.Objects;

public record BizQueryUseCaseProjectionTypeValidator(ExtractInterfaceParameterizedTypeResult extracted) {

    public BizQueryUseCaseProjectionTypeValidator {
        Objects.requireNonNull(extracted);
    }

    public boolean isInvalid() {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            final List<Type> arguments = extracted.parameterizedType().arguments();
            assert arguments.size() == 3;
            final org.jboss.jandex.Type projectionType = arguments.getFirst();
            final Class<?> projectionClass = classLoader.loadClass(projectionType.name().toString());
            return !VersionedProjection.class.isAssignableFrom(projectionClass) && !ListOfProjection.class.isAssignableFrom(projectionClass);
        } catch (final ClassNotFoundException classNotFoundException) {
            throw new IllegalStateException("Should not be here");
        }
    }

    public String implementorNaming() {
        return extracted.implementor().name().toString();
    }
}
