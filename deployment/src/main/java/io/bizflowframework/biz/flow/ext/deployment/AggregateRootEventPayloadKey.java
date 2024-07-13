package io.bizflowframework.biz.flow.ext.deployment;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRoot;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event.AggregateRootEventPayload;

import java.util.Objects;

public record AggregateRootEventPayloadKey(String aggregateRootClassName, String eventPayloadClassName) {
    public AggregateRootEventPayloadKey {
        Objects.requireNonNull(aggregateRootClassName);
        Objects.requireNonNull(eventPayloadClassName);
    }

    public AggregateRootEventPayloadKey(final Class<?> aggregateRootClass, final Class<?> eventPayloadClass) {
        this(Objects.requireNonNull(aggregateRootClass).getName(),
                Objects.requireNonNull(eventPayloadClass).getName());
        assert AggregateRoot.class.isAssignableFrom(aggregateRootClass);
        assert AggregateRootEventPayload.class.isAssignableFrom(eventPayloadClass);
    }

}
