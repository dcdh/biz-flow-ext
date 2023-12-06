package io.bizflowframework.biz.flow.ext.it.api;

import io.bizflowframework.biz.flow.ext.it.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.TodoStatus;

public record TodoDTO(String todoId,
                      String description,
                      TodoStatus status,
                      Integer version) {
    public TodoDTO(TodoAggregateRoot todoAggregateRoot) {
        this(todoAggregateRoot.aggregateId().id(),
                todoAggregateRoot.description(),
                todoAggregateRoot.status(),
                todoAggregateRoot.aggregateVersion().version());
    }

    public String getTodoId() {
        return todoId;
    }

    public String getDescription() {
        return description;
    }

    public TodoStatus getStatus() {
        return status;
    }

    public Integer getVersion() {
        return version;
    }
}
