package io.bizflowframework.biz.flow.ext.runtime;

import java.util.Objects;

public final class UnknownAggregateRootAtVersionException extends RuntimeException {
    private final AggregateId aggregateId;
    private final AggregateVersion aggregateVersion;
    public UnknownAggregateRootAtVersionException(final AggregateId aggregateId,
                                                  final AggregateVersion aggregateVersion) {
        this.aggregateId = Objects.requireNonNull(aggregateId);
        this.aggregateVersion = Objects.requireNonNull(aggregateVersion);
    }

    public AggregateId getAggregateId() {
        return aggregateId;
    }

    public AggregateVersion getAggregateVersion() {
        return aggregateVersion;
    }
}
