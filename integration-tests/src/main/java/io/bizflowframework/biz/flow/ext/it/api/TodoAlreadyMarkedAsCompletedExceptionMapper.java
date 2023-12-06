package io.bizflowframework.biz.flow.ext.it.api;

import io.bizflowframework.biz.flow.ext.it.TodoAlreadyMarkedAsCompletedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class TodoAlreadyMarkedAsCompletedExceptionMapper implements ExceptionMapper<TodoAlreadyMarkedAsCompletedException> {
    @Override
    public Response toResponse(final TodoAlreadyMarkedAsCompletedException exception) {
        return Response.status(Response.Status.CONFLICT)
                .type("application/vnd.todo-already-marked-as-completed-v1+json")
                .entity(String.format("Todo '%s' already marked as completed", exception.getTodoId().id()))
                .build();
    }
}
