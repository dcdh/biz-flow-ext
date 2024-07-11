package io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event;

import java.io.Serializable;
import java.util.Objects;

public record EventType(String type) implements Serializable {
    public EventType {
        Objects.requireNonNull(type);
    }

    public EventType(final Class<? extends AggregateRootEventPayload<?>> aggregateRootEventPayloadClass) {
        this(aggregateRootEventPayloadClass.getSimpleName());
    }
}
