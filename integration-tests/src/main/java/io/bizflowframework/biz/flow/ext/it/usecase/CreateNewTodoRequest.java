package io.bizflowframework.biz.flow.ext.it.usecase;

import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.runtime.usecase.Request;

import java.util.Objects;

public record CreateNewTodoRequest(String description) implements Request<TodoId> {
    public CreateNewTodoRequest {
        Objects.requireNonNull(description);
    }

    @Override
    public TodoId aggregateId() {
        return null;
    }
}
