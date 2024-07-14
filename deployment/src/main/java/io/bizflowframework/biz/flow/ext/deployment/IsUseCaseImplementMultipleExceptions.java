package io.bizflowframework.biz.flow.ext.deployment;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;

import java.util.List;
import java.util.function.Predicate;

public record IsUseCaseImplementMultipleExceptions() implements Predicate<ExtractedInterfaceParameterizedType> {

    @Override
    public boolean test(final ExtractedInterfaceParameterizedType extracted) {
        final List<Type> arguments = extracted.parameterizedType().arguments();
        assert arguments.size() == 3;
        final ClassInfo implementor = extracted.implementor();
        final MethodInfo execute = implementor.method("execute", arguments.get(1));
        return execute.exceptions().size() > 1;
    }
}
