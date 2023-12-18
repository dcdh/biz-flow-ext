package io.bizflowframework.biz.flow.ext.runtime.serde;

import io.bizflowframework.biz.flow.ext.runtime.AggregateRoot;
import io.bizflowframework.biz.flow.ext.runtime.event.AggregateRootEventPayload;

public interface AggregateRootEventPayloadSerde<T extends AggregateRoot<?, T>, P extends AggregateRootEventPayload<T>> {
    SerializedEventPayload serialize(P selfAggregateRootEventPayload);

    P deserialize(SerializedEventPayload serializedEventPayload);

    Class<T> aggregateRootClass();

    Class<P> aggregateRootEventPayloadClass();
}
