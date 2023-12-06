package io.bizflowframework.biz.flow.ext.runtime;

import java.util.Objects;

public record AggregateType(String type) {
    public AggregateType(final String type) {
        this.type = Objects.requireNonNull(type);
    }
    public AggregateType(final AggregateRoot aggregateRoot) {
        this(aggregateRoot.getClass().getSimpleName());
    }
}
