package io.bizflowframework.biz.flow.ext.it.api;

import io.bizflowframework.biz.flow.ext.it.UnknownTodoException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class UnknownTodoExceptionMapper implements ExceptionMapper<UnknownTodoException> {
    @Override
    public Response toResponse(final UnknownTodoException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .type("application/vnd.unknown-todo-v1+json")
                .entity(String.format("Todo '%s' is unknown", exception.getTodoId().id()))
                .build();
    }
}