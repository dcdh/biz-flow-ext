package io.bizflowframework.biz.flow.ext.runtime;

import java.util.Objects;

public final class UnknownAggregateRootException extends RuntimeException {
    private final AggregateId aggregateId;

    public UnknownAggregateRootException(final AggregateId aggregateId) {
        this.aggregateId = Objects.requireNonNull(aggregateId);
    }

    public AggregateId getAggregateId() {
        return aggregateId;
    }
}
