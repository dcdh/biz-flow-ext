package io.bizflowframework.biz.flow.ext.it.api;

import io.bizflowframework.biz.flow.ext.it.TodoAlreadyMarkedAsCompletedException;
import io.bizflowframework.biz.flow.ext.it.UnknownTodoException;
import io.bizflowframework.biz.flow.ext.it.usecase.MarkTodoAsCompletedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@Provider
public final class MarkTodoAsCompletedExceptionMapper implements ExceptionMapper<MarkTodoAsCompletedException> {
    private static final String VND_UNKNOWN_TODO_V1_TXT = "application/vnd.unknown-todo-v1+txt";
    private static final String VND_TODO_ALREADY_MARKED_AS_COMPLETED_V1_TXT = "application/vnd.todo-already-marked-as-completed-v1+txt";
    private static final String VND_MARK_TODO_AS_COMPLETED_ERROR_V1_TXT = "application/vnd.mark-todo-as-completed-error-v1+txt";

    private static final String TODO_UNKNOWN_MSG = "Todo '%s' is unknown";
    private static final String TODO_ALREADY_MARKED_AS_COMPLETED_MSG = "Todo '%s' already marked as completed";
    private static final String UNKNOWN_MSG = "Something wrong happened";

    @Override
    @APIResponses(
            value = {
                    @APIResponse(responseCode = "404", description = "Unknown Todo",
                            content = {
                                    @Content(
                                            mediaType = VND_UNKNOWN_TODO_V1_TXT,
                                            schema = @Schema(
                                                    implementation = String.class),
                                            examples = {
                                                    @ExampleObject(
                                                            name = "Unknown Todo",
                                                            value = "Todo '00000000-0000-0000-0000-000000000000' is unknown")
                                            }
                                    )
                            }
                    ),
                    @APIResponse(responseCode = "409", description = "Todo already marked as completed",
                            content = {
                                    @Content(
                                            mediaType = VND_TODO_ALREADY_MARKED_AS_COMPLETED_V1_TXT,
                                            schema = @Schema(
                                                    implementation = String.class),
                                            examples = {
                                                    @ExampleObject(
                                                            name = "Todo already marked as completed",
                                                            value = "Todo '00000000-0000-0000-0000-000000000000' already marked as completed")
                                            }
                                    )
                            }
                    ),
                    @APIResponse(responseCode = "500", description = "Something wrong happened",
                            content = {
                                    @Content(
                                            mediaType = VND_MARK_TODO_AS_COMPLETED_ERROR_V1_TXT,
                                            schema = @Schema(
                                                    implementation = String.class),
                                            examples = {
                                                    @ExampleObject(
                                                            name = "Something wrong happened",
                                                            value = "Something wrong happened")
                                            }
                                    )
                            }
                    )
            }
    )
    public Response toResponse(final MarkTodoAsCompletedException exception) {
        return switch (exception.getCause()) {
            case UnknownTodoException unknownTodoException -> Response.status(Response.Status.NOT_FOUND)
                    .type(VND_UNKNOWN_TODO_V1_TXT)
                    .entity(String.format(TODO_UNKNOWN_MSG, unknownTodoException.getTodoId().id()))
                    .build();
            case TodoAlreadyMarkedAsCompletedException todoAlreadyMarkedAsCompletedException ->
                    Response.status(Response.Status.CONFLICT)
                            .type(VND_TODO_ALREADY_MARKED_AS_COMPLETED_V1_TXT)
                            .entity(String.format(TODO_ALREADY_MARKED_AS_COMPLETED_MSG, todoAlreadyMarkedAsCompletedException.getTodoId().id()))
                            .build();
            default -> Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(VND_MARK_TODO_AS_COMPLETED_ERROR_V1_TXT)
                    .entity(UNKNOWN_MSG)
                    .build();
        };
    }
}
