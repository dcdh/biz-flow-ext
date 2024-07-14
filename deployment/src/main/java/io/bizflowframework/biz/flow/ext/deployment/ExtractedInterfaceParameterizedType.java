package io.bizflowframework.biz.flow.ext.deployment;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.ParameterizedType;

import java.util.Objects;

public record ExtractedInterfaceParameterizedType(ClassInfo implementor, ParameterizedType parameterizedType) {
    public ExtractedInterfaceParameterizedType {
        Objects.requireNonNull(implementor);
        Objects.requireNonNull(parameterizedType);
    }
}
