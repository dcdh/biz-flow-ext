package io.bizflowframework.biz.flow.ext.deployment;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;

import java.util.List;
import java.util.Objects;

public record UseCaseOnlyOneExceptionValidator(ExtractInterfaceParameterizedTypeResult extracted) {

    public UseCaseOnlyOneExceptionValidator {
        Objects.requireNonNull(extracted);
    }
// FCK je dois avoir une interface pour extraire le execute
    public boolean isInvalid() {
        final List<Type> arguments = extracted.parameterizedType().arguments();
        assert arguments.size() == 3;
        final ClassInfo implementor = extracted.implementor();
        final MethodInfo execute = implementor.method("execute", arguments.get(1));
        return execute.exceptions().size() > 1;
    }

    public String expectedExceptionNaming() {
        return extracted.implementor().simpleName() + "Exception";
    }

    public String implementorNaming() {
        return extracted.implementor().name().toString();
    }
}
