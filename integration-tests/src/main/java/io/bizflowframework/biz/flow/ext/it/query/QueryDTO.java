package io.bizflowframework.biz.flow.ext.it.query;

import io.bizflowframework.biz.flow.ext.it.TodoStatus;

public record QueryDTO(String todoId, String description, TodoStatus status, Integer version) {
    public QueryDTO(final QueryEntity queryEntity) {
        this(queryEntity.todoId().id(), queryEntity.description(), queryEntity.todoStatus(), queryEntity.version());
    }

    public String getTodoId() {
        return todoId;
    }

    public String getDescription() {
        return description;
    }

    public TodoStatus getTodoStatus() {
        return status;
    }

    public Integer getVersion() {
        return version;
    }
}
