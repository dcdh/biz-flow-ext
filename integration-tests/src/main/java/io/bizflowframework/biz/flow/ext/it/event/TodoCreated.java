package io.bizflowframework.biz.flow.ext.it.event;

import io.bizflowframework.biz.flow.ext.it.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.runtime.event.AggregateRootEventPayload;

import java.util.Objects;

public record TodoCreated(String description) implements AggregateRootEventPayload<TodoAggregateRoot> {
    public TodoCreated {
        Objects.requireNonNull(description);
    }

    @Override
    public void apply(final TodoAggregateRoot aggregateRoot) {
        aggregateRoot.on(this);
    }
}