package io.bizflowframework.biz.flow.ext.runtime.eventsourcing;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event.AggregateRootEventPayload;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event.EventType;

import java.io.Serializable;
import java.util.Objects;

public record AggregateRootDomainEvent<ID extends AggregateId, T extends AggregateRoot<ID, T>, P extends AggregateRootEventPayload<T>>(
        AggregateRootIdentifier<ID> aggregateRootIdentifier,
        AggregateVersion aggregateVersion,
        CreatedAt createdAt,
        P payload) implements Serializable {
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

    public P payload() {
        return payload;
    }
}
