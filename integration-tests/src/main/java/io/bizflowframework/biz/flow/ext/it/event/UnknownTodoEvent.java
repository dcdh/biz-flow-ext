package io.bizflowframework.biz.flow.ext.it.event;

import io.bizflowframework.biz.flow.ext.it.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.runtime.event.AggregateRootEventPayload;

public record UnknownTodoEvent() implements AggregateRootEventPayload<TodoId, TodoAggregateRoot> {
    @Override
    public void apply(final TodoAggregateRoot aggregateRoot) {
    }
}
