package io.bizflowframework.biz.flow.ext.test.event;

import io.bizflowframework.biz.flow.ext.runtime.event.AggregateRootEventPayload;
import io.bizflowframework.biz.flow.ext.test.TodoAggregateRoot;

public final class InvalidTodoEvent implements AggregateRootEventPayload<TodoAggregateRoot> {
    @Override
    public void apply(TodoAggregateRoot aggregateRoot) {

    }
}
