package io.bizflowframework.biz.flow.ext.it;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;

import java.util.Objects;

public record TodoId(String id) implements AggregateId {
    public TodoId {
        Objects.requireNonNull(id);
    }
}
