package io.bizflowframework.biz.flow.ext.deployment;

import org.jboss.jandex.MethodInfo;

import java.util.List;
import java.util.function.Predicate;

public record IsUseCaseExceptionNamingValid() implements Predicate<ExtractedInterfaceParameterizedType> {

    @Override
    public boolean test(final ExtractedInterfaceParameterizedType extracted) {
        final String expectedExceptionNaming = expectedExceptionNaming(extracted);
        final String exceptionNaming = exceptionNaming(extracted);
        return expectedExceptionNaming.equals(exceptionNaming);
    }

    public static String expectedExceptionNaming(final ExtractedInterfaceParameterizedType extracted) {
        return extracted.implementor().simpleName() + "Exception";
    }

    public static String exceptionNaming(final ExtractedInterfaceParameterizedType extracted) {
        final List<org.jboss.jandex.Type> arguments = extracted.parameterizedType().arguments();
        final MethodInfo execute = extracted.implementor().method("execute", arguments.get(1));
        return new ExtractClassNaming().apply(execute.exceptions().getFirst());
    }
}
