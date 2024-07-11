package io.bizflowframework.biz.flow.ext.it.domain.event;

import io.bizflowframework.biz.flow.ext.it.domain.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event.AggregateRootEventPayload;

public record TodoMarkedAsCompleted() implements AggregateRootEventPayload<TodoAggregateRoot> {
    @Override
    public void apply(final TodoAggregateRoot aggregateRoot) {
        aggregateRoot.on(this);
    }
}
