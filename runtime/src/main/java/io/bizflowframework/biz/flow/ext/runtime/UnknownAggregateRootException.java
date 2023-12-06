package io.bizflowframework.biz.flow.ext.runtime;

import java.util.Objects;

public final class UnknownAggregateRootException extends RuntimeException {
    private final AggregateRootIdentifier<?> aggregateRootIdentifier;

    public UnknownAggregateRootException(final AggregateRootIdentifier<?> aggregateRootIdentifier) {
        this.aggregateRootIdentifier = Objects.requireNonNull(aggregateRootIdentifier);
    }

    public AggregateRootIdentifier<?> getAggregateRootIdentifier() {
        return aggregateRootIdentifier;
    }
}
