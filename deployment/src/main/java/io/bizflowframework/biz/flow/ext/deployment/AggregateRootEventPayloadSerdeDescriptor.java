package io.bizflowframework.biz.flow.ext.deployment;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRoot;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event.AggregateRootEventPayload;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.AggregateRootEventPayloadSerde;

import java.util.Objects;

public record AggregateRootEventPayloadSerdeDescriptor(Class<?> aggregateRootEventPayloadSerde,
                                                       Class<?> aggregateRootClass,
                                                       Class<?> eventPayloadClass) {
    public AggregateRootEventPayloadSerdeDescriptor {
        Objects.requireNonNull(aggregateRootEventPayloadSerde);
        Objects.requireNonNull(aggregateRootClass);
        Objects.requireNonNull(eventPayloadClass);
        assert AggregateRootEventPayloadSerde.class.isAssignableFrom(aggregateRootEventPayloadSerde);
        assert AggregateRoot.class.isAssignableFrom(aggregateRootClass);
        assert AggregateRootEventPayload.class.isAssignableFrom(eventPayloadClass);
    }

    public boolean belongsTo(final AggregateRootEventPayloadDescriptor aggregateRootEventPayloadDescriptor) {
        return aggregateRootClass.equals(aggregateRootEventPayloadDescriptor.aggregateRootClass())
                && eventPayloadClass.equals(aggregateRootEventPayloadDescriptor.eventPayloadClass());
    }

    public AggregateRootEventPayloadDescriptor toAggregateRootEventPayloadDescriptor() {
        return new AggregateRootEventPayloadDescriptor(aggregateRootClass, eventPayloadClass);
    }
}