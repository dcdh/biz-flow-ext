package io.bizflowframework.biz.flow.ext.it.infra.api;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;
import java.util.Objects;

@Schema(name = "ListOfTodos", required = true, requiredProperties = {"todos", "nbOfElements"})
public record ListOfTodosDTO(List<TodoDTO> todos, Long nbOfElements) {
    public ListOfTodosDTO {
        Objects.requireNonNull(todos);
        Objects.requireNonNull(nbOfElements);
    }
}
