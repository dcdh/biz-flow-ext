package io.bizflowframework.biz.flow.ext.deployment;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.ParameterizedType;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public record ExtractInterfaceParameterizedTypeFromClassInfo(
        List<Class<?>> interfacesToFind) implements Function<ClassInfo, ParameterizedType> {

    public ExtractInterfaceParameterizedTypeFromClassInfo(final Class<?>... interfacesToFind) {
        this(Arrays.asList(interfacesToFind));
    }

    public ExtractInterfaceParameterizedTypeFromClassInfo(final Class<?> interfaceToFind) {
        this(List.of(interfaceToFind));
    }

    @Override
    public ParameterizedType apply(final ClassInfo classInfo) {
        final List<DotName> interfaceDotNames = classInfo.interfaceNames();
        final int position = IntStream.range(0, interfaceDotNames.size())
                .filter(index -> interfacesToFind
                        .stream().map(Class::getName)
                        .anyMatch(interfaceToFind -> interfaceToFind.equals(interfaceDotNames.get(index).toString())))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Should not be here"));
        return (ParameterizedType) classInfo.interfaceTypes().get(position);
    }
}
