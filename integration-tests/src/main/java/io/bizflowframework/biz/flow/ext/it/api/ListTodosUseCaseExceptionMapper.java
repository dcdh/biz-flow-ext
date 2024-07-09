package io.bizflowframework.biz.flow.ext.it.api;

import io.bizflowframework.biz.flow.ext.it.usecase.ListTodosUseCaseException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

@Provider
public class ListTodosUseCaseExceptionMapper implements ExceptionMapper<ListTodosUseCaseException> {
    private static final String UNKNOWN_MSG = "Something wrong happened";
    private static final String VND_LIST_TODOS_ERROR_V1_TXT = "application/vnd.list-todos-error-v1+txt";

    @Override
    @APIResponses(
            value = {
                    @APIResponse(responseCode = "500", description = UNKNOWN_MSG,
                            content = {
                                    @Content(
                                            mediaType = VND_LIST_TODOS_ERROR_V1_TXT,
                                            schema = @Schema(
                                                    implementation = String.class),
                                            examples = {
                                                    @ExampleObject(
                                                            name = UNKNOWN_MSG,
                                                            value = UNKNOWN_MSG)
                                            }
                                    )
                            }
                    )
            }
    )
    public Response toResponse(final ListTodosUseCaseException exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(VND_LIST_TODOS_ERROR_V1_TXT)
                .entity(UNKNOWN_MSG)
                .build();
    }
}
