package io.bizflowframework.biz.flow.ext.it.event;

import io.bizflowframework.biz.flow.ext.it.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.runtime.event.AggregateRootEventPayload;

public record TodoMarkedAsCompleted() implements AggregateRootEventPayload<TodoAggregateRoot> {
    @Override
    public void apply(final TodoAggregateRoot aggregateRoot) {
        aggregateRoot.on(this);
    }
}
