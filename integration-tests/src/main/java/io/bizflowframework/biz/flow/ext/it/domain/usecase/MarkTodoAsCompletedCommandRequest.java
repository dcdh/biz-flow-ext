package io.bizflowframework.biz.flow.ext.it.domain.usecase;

import io.bizflowframework.biz.flow.ext.it.domain.TodoId;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.command.AggregateCommandRequest;

import java.util.Objects;

public record MarkTodoAsCompletedCommandRequest(TodoId todoId) implements AggregateCommandRequest<TodoId> {
    public MarkTodoAsCompletedCommandRequest {
        Objects.requireNonNull(todoId);
    }

    @Override
    public TodoId aggregateId() {
        return todoId;
    }
}
