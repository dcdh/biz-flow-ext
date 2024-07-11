package io.bizflowframework.biz.flow.ext.it.domain.usecase;

import io.bizflowframework.biz.flow.ext.it.domain.TodoId;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.command.AggregateCommandRequest;

import java.util.Objects;

public record MarkTodoAsCompletedRequest(TodoId todoId) implements AggregateCommandRequest<TodoId> {
    public MarkTodoAsCompletedRequest {
        Objects.requireNonNull(todoId);
    }

    @Override
    public TodoId aggregateId() {
        return todoId;
    }
}
