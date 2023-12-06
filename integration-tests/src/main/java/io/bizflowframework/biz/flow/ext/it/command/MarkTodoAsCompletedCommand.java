package io.bizflowframework.biz.flow.ext.it.command;

import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.runtime.command.Command;

import java.util.Objects;

public record MarkTodoAsCompletedCommand(TodoId todoId) implements Command<TodoId> {
    public MarkTodoAsCompletedCommand {
        Objects.requireNonNull(todoId);
    }

    @Override
    public TodoId aggregateId() {
        return todoId;
    }
}
