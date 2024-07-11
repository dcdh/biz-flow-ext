package io.bizflowframework.biz.flow.ext.it.domain.usecase;

import io.bizflowframework.biz.flow.ext.runtime.usecase.CommandRequest;

import java.util.Objects;

public record CreateNewTodoRequest(String description) implements CommandRequest {
    public CreateNewTodoRequest {
        Objects.requireNonNull(description);
    }

}
