package io.bizflowframework.biz.flow.ext.it.api;

import io.bizflowframework.biz.flow.ext.it.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.TodoStatus;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "Sensor", required = true, requiredProperties = {"todoId", "description", "status", "version"})
public record TodoDTO(String todoId, String description, TodoStatus status, Integer version) {

    public TodoDTO(TodoAggregateRoot todoAggregateRoot) {
        this(todoAggregateRoot.aggregateId().id(),
                todoAggregateRoot.description(),
                todoAggregateRoot.status(),
                todoAggregateRoot.aggregateVersion().version());
    }
}
