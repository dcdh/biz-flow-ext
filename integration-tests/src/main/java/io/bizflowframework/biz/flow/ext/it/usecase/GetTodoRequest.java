package io.bizflowframework.biz.flow.ext.it.usecase;

import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.runtime.usecase.QueryRequest;

import java.util.Objects;

public record GetTodoRequest(TodoId todoId) implements QueryRequest {
    public GetTodoRequest {
        Objects.requireNonNull(todoId);
    }
}
