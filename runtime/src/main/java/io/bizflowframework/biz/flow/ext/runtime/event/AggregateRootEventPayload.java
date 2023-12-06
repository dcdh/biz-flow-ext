package io.bizflowframework.biz.flow.ext.runtime.event;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.AggregateRoot;

import java.io.Serializable;

public interface AggregateRootEventPayload<ID extends AggregateId, T extends AggregateRoot<ID,T>> extends Serializable {
    void apply(T aggregateRoot);

    default EventType eventType() {
        return new EventType(this.getClass().getSimpleName());
    }
}
