package io.bizflowframework.biz.flow.ext.it.command;

import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.runtime.command.Command;

import java.util.Objects;

public record CreateNewTodoCommand(String description) implements Command<TodoId> {
    public CreateNewTodoCommand {
        Objects.requireNonNull(description);
    }

    @Override
    public TodoId aggregateId() {
        return null;
    }
}
