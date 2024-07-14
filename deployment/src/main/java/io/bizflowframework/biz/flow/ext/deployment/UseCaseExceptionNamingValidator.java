package io.bizflowframework.biz.flow.ext.deployment;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;

import java.util.List;
import java.util.Objects;

public record UseCaseExceptionNamingValidator(ExtractInterfaceParameterizedTypeResult extracted) {

    public UseCaseExceptionNamingValidator {
        Objects.requireNonNull(extracted);
    }

    public boolean hasOnlyOneExceptionDefined() {
        final List<Type> arguments = extracted.parameterizedType().arguments();
        assert arguments.size() == 3;
        final ClassInfo implementor = extracted.implementor();
        final MethodInfo execute = implementor.method("execute", arguments.get(1));
        return execute.exceptions().size() == 1;
    }

    public boolean isInvalid() {
        // TODO FCK extraire je devrais avoir un CONTEXT
        final String expectedExceptionNaming = expectedExceptionNaming();
        return !expectedExceptionNaming.equals(exceptionNaming());
    }

    public boolean isUseCaseWellDefined() {
        // FCK TODO shared
        return extracted.implementor().simpleName().endsWith(BizMutationUseCaseRequestCommandValidator.BIZ_MUTATION_USE_CASE_SIMPLE_NAME)
                || extracted.implementor().simpleName().endsWith(BizQueryUseCaseRequestCommandValidator.BIZ_QUERY_USE_CASE_SIMPLE_NAME);
    }

    public String expectedExceptionNaming() {
        return extracted.implementor().simpleName() + "Exception";
    }

    public String exceptionNaming() {
        final List<org.jboss.jandex.Type> arguments = extracted.parameterizedType().arguments();
        final MethodInfo execute = extracted.implementor().method("execute", arguments.get(1));
        return new ExtractClassNaming().apply(execute.exceptions().getFirst());
    }

    public String implementorName() {
        return extracted.implementor().name().toString();
    }

    public String implementorSimpleName() {
        return extracted.implementor().simpleName();
    }
}
