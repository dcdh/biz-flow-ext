package io.bizflowframework.biz.flow.ext.runtime.serde;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.AggregateRoot;
import io.bizflowframework.biz.flow.ext.runtime.event.AggregateRootEventPayload;

public interface AggregateRootEventPayloadSerde<ID extends AggregateId, T extends AggregateRoot<ID, T>, P extends AggregateRootEventPayload<ID, T>> {
    SerializedEventPayload serialize(P selfAggregateRootEventPayload);

    P deserialize(SerializedEventPayload serializedEventPayload);

    Class<T> aggregateRootClass();

    Class<P> aggregateRootEventPayloadClass();
}
