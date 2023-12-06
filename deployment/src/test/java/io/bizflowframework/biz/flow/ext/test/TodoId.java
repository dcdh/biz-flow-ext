package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;

import java.util.Objects;

public record TodoId(String testName) implements AggregateId {
    public TodoId {
        Objects.requireNonNull(testName);
    }

    @Override
    public String id() {
        return testName;
    }
}
