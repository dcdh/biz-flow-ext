package io.bizflowframework.biz.flow.ext.deployment;

import java.util.function.Predicate;

public record IsClassARecord() implements Predicate<Class<?>> {

    @Override
    public boolean test(final Class<?> clazz) {
        return clazz.isRecord();
    }
}
