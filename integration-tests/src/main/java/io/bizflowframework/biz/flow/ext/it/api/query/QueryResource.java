package io.bizflowframework.biz.flow.ext.it.api.query;

import io.bizflowframework.biz.flow.ext.it.TodoId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import java.util.Objects;

@Path("/query")
@ApplicationScoped
public class QueryResource {
    private final QueryService queryService;

    public QueryResource(final QueryService queryService) {
        this.queryService = Objects.requireNonNull(queryService);
    }

    @GET
    @Produces("application/vnd.query-todo-v1+json")
    @Path("/{todoId}")
    public QueryDTO getByTodoId(@PathParam("todoId") final TodoId todoId) {
        return new QueryDTO(queryService.getByTodoId(todoId));
    }

}
