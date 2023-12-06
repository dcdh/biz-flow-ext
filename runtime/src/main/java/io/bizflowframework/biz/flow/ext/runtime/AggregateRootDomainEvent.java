package io.bizflowframework.biz.flow.ext.runtime;

import java.util.Objects;

public record AggregateRootDomainEvent<T extends AggregateRoot<T>>(AggregateId aggregateId,
                                                                   AggregateType aggregateType,
                                                                   AggregateVersion aggregateVersion,
                                                                   CreatedAt createdAt,
                                                                   AggregateRootEventPayload<T> payload) {
    public AggregateRootDomainEvent {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(aggregateType);
        Objects.requireNonNull(aggregateVersion);
        Objects.requireNonNull(createdAt);
        Objects.requireNonNull(payload);
    }
}
