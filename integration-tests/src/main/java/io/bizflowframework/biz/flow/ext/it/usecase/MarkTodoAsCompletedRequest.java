package io.bizflowframework.biz.flow.ext.it.usecase;

import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.runtime.command.AggregateCommandRequest;

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
