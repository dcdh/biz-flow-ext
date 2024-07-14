package io.bizflowframework.biz.flow.ext.deployment;

import org.jboss.jandex.ClassInfo;

import java.util.function.Predicate;

public record IsEventNameValid() implements Predicate<ClassInfo> {

    @Override
    public boolean test(final ClassInfo implementor) {
        return implementor.simpleName().endsWith("Event");
    }
}
