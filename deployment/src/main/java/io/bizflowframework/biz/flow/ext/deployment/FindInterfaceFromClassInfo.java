package io.bizflowframework.biz.flow.ext.deployment;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public record FindInterfaceFromClassInfo(List<Class<?>> interfacesToFind) implements Function<ClassInfo, Integer> {

    public FindInterfaceFromClassInfo(final Class<?>... interfacesToFind) {
        this(Arrays.asList(interfacesToFind));
    }

    public FindInterfaceFromClassInfo(final Class<?> interfaceToFind) {
        this(List.of(interfaceToFind));
    }

    @Override
    public Integer apply(final ClassInfo classInfo) {
        final List<DotName> interfaceDotNames = classInfo.interfaceNames();
        return IntStream.range(0, interfaceDotNames.size())
                .filter(index -> interfacesToFind
                        .stream().map(Class::getName)
                        .anyMatch(interfaceToFind -> interfaceToFind.equals(interfaceDotNames.get(index).toString())))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Should not be here"));
    }
}
