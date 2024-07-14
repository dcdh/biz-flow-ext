package io.bizflowframework.biz.flow.ext.deployment;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.ParameterizedType;

import java.util.Objects;

public record ExtractInterfaceParameterizedTypeResult(ClassInfo implementor, ParameterizedType parameterizedType) {
    public ExtractInterfaceParameterizedTypeResult {
        Objects.requireNonNull(implementor);
        Objects.requireNonNull(parameterizedType);
    }
}
