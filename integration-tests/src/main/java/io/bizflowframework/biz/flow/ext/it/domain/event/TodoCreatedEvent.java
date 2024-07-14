package io.bizflowframework.biz.flow.ext.it.domain.event;

import io.bizflowframework.biz.flow.ext.it.domain.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event.AggregateRootEventPayload;

import java.util.Objects;

public record TodoCreatedEvent(String description) implements AggregateRootEventPayload<TodoAggregateRoot> {
    public TodoCreatedEvent {
        Objects.requireNonNull(description);
    }

    @Override
    public void apply(final TodoAggregateRoot aggregateRoot) {
        aggregateRoot.on(this);
    }
}