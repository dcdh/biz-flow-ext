package io.bizflowframework.biz.flow.ext.deployment;

import io.bizflowframework.biz.flow.ext.runtime.usecase.BizMutationUseCase;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.Type;

import java.util.List;
import java.util.function.Predicate;

public record IsBizMutationUseCaseRequestCommandValid() implements Predicate<ExtractedInterfaceParameterizedType> {
    private static final String BIZ_MUTATION_USE_CASE_SIMPLE_NAME = BizMutationUseCase.class.getSimpleName();

    @Override
    public boolean test(final ExtractedInterfaceParameterizedType extracted) {
        return commandRequestCurrentNaming(extracted).equals(commandRequestExpectedNaming(extracted));
    }

    public static String commandRequestCurrentNaming(final ExtractedInterfaceParameterizedType extracted) {
        final ParameterizedType parameterizedType = extracted.parameterizedType();
        final List<Type> arguments = parameterizedType.arguments();
        assert arguments.size() == 3;
        return new ExtractClassNaming().apply(arguments.get(1));
    }

    public static String commandRequestExpectedNaming(final ExtractedInterfaceParameterizedType extracted) {
        final int indexOfBizQueryUseCase = extracted.implementor().simpleName().indexOf(BIZ_MUTATION_USE_CASE_SIMPLE_NAME);
        return extracted.implementor().simpleName().substring(0, indexOfBizQueryUseCase) + "CommandRequest";
    }
}
