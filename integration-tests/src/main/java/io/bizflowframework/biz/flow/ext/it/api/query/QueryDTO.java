package io.bizflowframework.biz.flow.ext.it.api.query;

import io.bizflowframework.biz.flow.ext.it.TodoStatus;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "Query", required = true, requiredProperties = {"todoId", "description", "status", "version"})
public record QueryDTO(String todoId, String description, TodoStatus status, Integer version) {
    public QueryDTO(final QueryEntity queryEntity) {
        this(queryEntity.todoId().id(), queryEntity.description(), queryEntity.todoStatus(), queryEntity.version());
    }

}
