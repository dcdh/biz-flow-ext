package io.bizflowframework.biz.flow.ext.runtime;

import io.bizflowframework.biz.flow.ext.runtime.event.AggregateRootEventPayload;
import io.bizflowframework.biz.flow.ext.runtime.event.EventType;

import java.io.Serializable;
import java.util.Objects;

public record AggregateRootDomainEvent<ID extends AggregateId, T extends AggregateRoot<ID, T>>(
        AggregateRootIdentifier<ID> aggregateRootIdentifier,
        AggregateVersion aggregateVersion,
        CreatedAt createdAt,
        AggregateRootEventPayload<T> payload) implements Serializable {
    public AggregateRootDomainEvent {
        Objects.requireNonNull(aggregateRootIdentifier);
        Objects.requireNonNull(aggregateVersion);
        Objects.requireNonNull(createdAt);
        Objects.requireNonNull(payload);
    }

    public EventType eventType() {
        return payload.eventType();
    }

    public AggregateRootIdentifier<ID> aggregateRootIdentifier() {
        return aggregateRootIdentifier;
    }

    public AggregateType aggregateType() {
        return aggregateRootIdentifier.aggregateType();
    }

    public AggregateVersion aggregateVersion() {
        return aggregateVersion;
    }

    public CreatedAt createdAt() {
        return createdAt;
    }

    public AggregateRootEventPayload<T> payload() {
        return payload;
    }
}
