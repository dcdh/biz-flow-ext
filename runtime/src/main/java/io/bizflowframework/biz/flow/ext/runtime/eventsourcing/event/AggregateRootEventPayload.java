package io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRoot;

import java.io.Serializable;

public interface AggregateRootEventPayload<T extends AggregateRoot<?, T>> extends Serializable {
    void apply(T aggregateRoot);

    default EventType eventType() {
        return new EventType(this.getClass().getSimpleName());
    }
}
