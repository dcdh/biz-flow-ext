package io.bizflowframework.biz.flow.ext.it.domain.usecase;

import io.bizflowframework.biz.flow.ext.runtime.usecase.CommandRequest;

import java.util.Objects;

public record CreateNewTodoCommandRequest(String description) implements CommandRequest {
    public CreateNewTodoCommandRequest {
        Objects.requireNonNull(description);
    }

}
