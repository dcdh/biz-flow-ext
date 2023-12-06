package io.bizflowframework.biz.flow.ext.it;

import java.util.Objects;

public final class UnknownTodoException extends RuntimeException {
    private final TodoId todoId;

    public UnknownTodoException(final TodoId todoId) {
        this.todoId = Objects.requireNonNull(todoId);
    }

    public TodoId getTodoId() {
        return todoId;
    }
}
