package io.bizflowframework.biz.flow.ext.deployment;

import io.bizflowframework.biz.flow.ext.runtime.usecase.BizQueryUseCase;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.Type;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public record BizQueryUseCaseRequestCommandValidator(ClassInfo classInfo) {
    public static final String BIZ_QUERY_USE_CASE_SIMPLE_NAME = BizQueryUseCase.class.getSimpleName();

    public BizQueryUseCaseRequestCommandValidator {
        Objects.requireNonNull(classInfo);
        try {
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            assert BizQueryUseCase.class.isAssignableFrom(classLoader.loadClass(classInfo.name().toString()));
        } catch (final ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    public boolean canValidate() {
        return classInfo.simpleName().contains(BIZ_QUERY_USE_CASE_SIMPLE_NAME);
    }

    public String useCaseNaming() {
        return classInfo.name().toString();
    }

    public boolean isInvalid() {
        return !queryRequestCurrentNaming().equals(queryRequestExpectedNaming());
    }

    public String queryRequestCurrentNaming() {
        final List<DotName> interfaceDotNames = classInfo.interfaceNames();
        final int position = IntStream.range(0, interfaceDotNames.size())
                .filter(index -> BizQueryUseCase.class.getName().equals(interfaceDotNames.get(index).toString()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Should not be here"));
        final ParameterizedType parameterizedType = (ParameterizedType) classInfo.interfaceTypes().get(position);
        final List<Type> arguments = parameterizedType.arguments();
        assert arguments.size() == 3;
        return new ExtractClassNaming().apply(arguments.get(1));
    }

    public String queryRequestExpectedNaming() {
        final int indexOfBizQueryUseCase = classInfo.simpleName().indexOf(BIZ_QUERY_USE_CASE_SIMPLE_NAME);
        return classInfo.simpleName().substring(0, indexOfBizQueryUseCase) + "QueryRequest";
    }
}
