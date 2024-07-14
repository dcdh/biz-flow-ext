package io.bizflowframework.biz.flow.ext.deployment;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRoot;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event.AggregateRootEventPayload;

import java.util.Objects;

public record AggregateRootEventPayloadDescriptor(Class<?> aggregateRootClass, Class<?> eventPayloadClass) {
    public AggregateRootEventPayloadDescriptor {
        Objects.requireNonNull(aggregateRootClass);
        Objects.requireNonNull(eventPayloadClass);
        assert AggregateRoot.class.isAssignableFrom(aggregateRootClass);
        assert AggregateRootEventPayload.class.isAssignableFrom(eventPayloadClass);
    }

}
