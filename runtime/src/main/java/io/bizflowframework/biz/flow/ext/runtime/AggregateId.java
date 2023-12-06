package io.bizflowframework.biz.flow.ext.runtime;

import java.util.Objects;

public record AggregateId(String id) {
    public AggregateId {
        Objects.requireNonNull(id);
    }
}
