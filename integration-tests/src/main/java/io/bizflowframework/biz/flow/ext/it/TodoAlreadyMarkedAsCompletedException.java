package io.bizflowframework.biz.flow.ext.it;

import java.util.Objects;

public class TodoAlreadyMarkedAsCompletedException extends RuntimeException {
    private final TodoId todoId;

    public TodoAlreadyMarkedAsCompletedException(final TodoId todoId) {
        this.todoId = Objects.requireNonNull(todoId);
    }

    public TodoId getTodoId() {
        return todoId;
    }
}
