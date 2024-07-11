package io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateType;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event.EventType;

import java.util.Objects;

public final class MissingSerdeException extends RuntimeException {
    private final AggregateType aggregateType;
    private final EventType eventType;

    public MissingSerdeException(final AggregateType aggregateType,
                                 final EventType eventType) {
        this.aggregateType = Objects.requireNonNull(aggregateType);
        this.eventType = Objects.requireNonNull(eventType);
    }

    public AggregateType getAggregateType() {
        return aggregateType;
    }

    public EventType getEventType() {
        return eventType;
    }
}
