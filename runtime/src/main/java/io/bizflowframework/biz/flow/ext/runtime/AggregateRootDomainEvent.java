package io.bizflowframework.biz.flow.ext.runtime;

import io.bizflowframework.biz.flow.ext.runtime.event.AggregateRootEventPayload;
import io.bizflowframework.biz.flow.ext.runtime.event.EventType;

import java.io.Serializable;
import java.util.Objects;

public record AggregateRootDomainEvent<ID extends AggregateId, T extends AggregateRoot<ID, T>>(
        AggregateRootIdentifier<ID> aggregateRootIdentifier,
        AggregateVersion aggregateVersion,
        CreatedAt createdAt,
        AggregateRootEventPayload<ID, T> payload) implements Serializable {
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

    public AggregateRootEventPayload<ID, T> payload() {
        return payload;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AggregateRootDomainEvent<?, ?>) obj;
        return Objects.equals(this.aggregateRootIdentifier, that.aggregateRootIdentifier) &&
               Objects.equals(this.aggregateVersion, that.aggregateVersion) &&
               Objects.equals(this.createdAt, that.createdAt) &&
               Objects.equals(this.payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootIdentifier, aggregateVersion, createdAt, payload);
    }

}
