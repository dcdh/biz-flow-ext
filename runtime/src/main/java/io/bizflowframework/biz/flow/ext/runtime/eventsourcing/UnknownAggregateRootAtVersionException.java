package io.bizflowframework.biz.flow.ext.runtime.eventsourcing;

import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;

import java.util.Objects;

public final class UnknownAggregateRootAtVersionException extends RuntimeException {
    private final AggregateRootIdentifier<?> aggregateRootIdentifier;
    private final AggregateVersion aggregateVersion;

    public UnknownAggregateRootAtVersionException(final AggregateRootIdentifier<?> aggregateRootIdentifier,
                                                  final AggregateVersion aggregateVersion) {
        this.aggregateRootIdentifier = Objects.requireNonNull(aggregateRootIdentifier);
        this.aggregateVersion = Objects.requireNonNull(aggregateVersion);
    }

    public AggregateRootIdentifier<?> getAggregateRootIdentifier() {
        return aggregateRootIdentifier;
    }

    public AggregateVersion getAggregateVersion() {
        return aggregateVersion;
    }
}
