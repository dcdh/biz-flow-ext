package io.bizflowframework.biz.flow.ext.deployment;

import org.jboss.jandex.Type;

import java.util.function.Function;

public record ExtractClassNaming() implements Function<Type, String> {

    @Override
    public String apply(final Type type) {
        final String className = type.name().withoutPackagePrefix();
        if (className.contains("$")) {
            return className.substring(className.lastIndexOf('$') + 1);
        }
        return className;
    }
}
