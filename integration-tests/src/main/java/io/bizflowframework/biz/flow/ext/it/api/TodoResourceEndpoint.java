package io.bizflowframework.biz.flow.ext.it.api;

import io.bizflowframework.biz.flow.ext.it.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.it.usecase.*;
import io.bizflowframework.biz.flow.ext.runtime.AggregateRootRepository;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.CreatedAtProvider;
import io.bizflowframework.biz.flow.ext.runtime.EventStoreException;
import io.bizflowframework.biz.flow.ext.runtime.incrementer.AggregateVersionIncrementer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

@Path("/todo")
@ApplicationScoped
public class TodoResourceEndpoint {
    private final CreateNewTodoUseCase createNewTodoCommandHandler;
    private final MarkTodoAsCompletedUseCase markTodoAsCompletedCommandHandler;
    private final AggregateRootRepository<TodoId, TodoAggregateRoot> aggregateAggregateRootRepository;
    private final CreatedAtProvider createdAtProvider;
    private final AggregateVersionIncrementer aggregateVersionIncrementer;
    private final DataSource dataSource;

    public TodoResourceEndpoint(final CreateNewTodoUseCase createNewTodoUseCase,
                                final MarkTodoAsCompletedUseCase markTodoAsCompletedUseCase,
                                final AggregateRootRepository<TodoId, TodoAggregateRoot> todoAggregateRootRepository,
                                final CreatedAtProvider createdAtProvider,
                                final AggregateVersionIncrementer aggregateVersionIncrementer,
                                final DataSource dataSource) {
        this.createNewTodoCommandHandler = Objects.requireNonNull(createNewTodoUseCase);
        this.markTodoAsCompletedCommandHandler = Objects.requireNonNull(markTodoAsCompletedUseCase);
        this.aggregateAggregateRootRepository = Objects.requireNonNull(todoAggregateRootRepository);
        this.createdAtProvider = Objects.requireNonNull(createdAtProvider);
        this.aggregateVersionIncrementer = Objects.requireNonNull(aggregateVersionIncrementer);
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("application/vnd.todo-v1+json")
    @Path("/createNewTodo")
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_FORM_URLENCODED,
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            requiredProperties = {"description"},
                            properties = {
                                    @SchemaProperty(
                                            name = "description",
                                            type = SchemaType.STRING,
                                            description = "Description on what to do",
                                            example = "Finish this extension :)"
                                    )
                            }
                    ),
                    // list of example not displayed when trying it
                    // https://github.com/swagger-api/swagger-ui/issues/10051
                    examples = {
                            @ExampleObject(
                                    name = "lorem ipsum",
                                    value = "lorem ipsum dolor sit amet"
                            ),
                            @ExampleObject(
                                    name = "extension",
                                    value = "Finish it !"
                            )
                    }
            )
    )
    @APIResponses(
            value = {
                    @APIResponse(
                            name = "success",
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(
                                            type = SchemaType.OBJECT,
                                            implementation = TodoDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Todo response",
                                                    //language=JSON
                                                    value = """
                                                            {
                                                              "todoId": "00000000-0000-0000-0000-000000000000",
                                                              "description": "lorem ipsum dolor sit amet",
                                                              "status": "IN_PROGRESS",
                                                              "version": 0
                                                            }
                                                            """
                                            )
                                    }
                            )
                    )
            }
    )
    public TodoDTO createNewTodo(@FormParam("description") final String description) throws CreateNewTodoException {
        final TodoAggregateRoot todoCreated = createNewTodoCommandHandler.execute(new CreateNewTodoRequest(description));
        return new TodoDTO(todoCreated);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("application/vnd.todo-v1+json")
    @Path("/markTodoAsCompleted")
    @APIResponses(
            value = {
                    @APIResponse(
                            name = "success",
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(
                                            type = SchemaType.OBJECT,
                                            implementation = TodoDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Sensor following temperature taken",
                                                    //language=JSON
                                                    value = """
                                                            {
                                                              "todoId": "00000000-0000-0000-0000-000000000000",
                                                              "description": "lorem ipsum dolor sit amet",
                                                              "status": "COMPLETED",
                                                              "version": 1
                                                            }
                                                            """
                                            )
                                    }
                            )
                    )
            }
    )
    public TodoDTO markTodoAsCompleted(@FormParam("todoId") final String todoId) throws MarkTodoAsCompletedException {
        final TodoAggregateRoot todoCompleted = markTodoAsCompletedCommandHandler.execute(new MarkTodoAsCompletedRequest(new TodoId(todoId)));
        return new TodoDTO(todoCompleted);
    }

    @POST
    @Path("/failMissingSerde")
    public void failMissingSerde() {
        final TodoAggregateRoot givenTodoAggregateRoot = new TodoAggregateRoot(
                new TodoId("nice_todo"), createdAtProvider, aggregateVersionIncrementer);
        givenTodoAggregateRoot.addUnknownTodoEvent();
        aggregateAggregateRootRepository.save(givenTodoAggregateRoot);
    }

    @POST
    @Path("/failUnknownAggregate")
    public void failUnknownAggregate() {
        aggregateAggregateRootRepository.load(new TodoId("unknown"));
    }

    @POST
    @Path("/failUnknownAggregateAtVersion")
    public void failUnknownAggregateAtVersion() {
        final AggregateVersion givenUnknownAggregateVersion = new AggregateVersion(10);
        final TodoAggregateRoot givenTodoAggregateRoot = new TodoAggregateRoot(
                new TodoId("todo_to_fail_at_unknown_version"), createdAtProvider, aggregateVersionIncrementer);
        givenTodoAggregateRoot.handle(new CreateNewTodoRequest("lorem ipsum dolor sit amet"));
        aggregateAggregateRootRepository.save(givenTodoAggregateRoot);
        aggregateAggregateRootRepository.load(new TodoId("todo_to_fail_at_unknown_version"), givenUnknownAggregateVersion);
    }

    @POST
    @Path("/failOnActionForbidden")
    public void failOnActionForbidden() {
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement deletePreparedStatement = connection.prepareStatement("TRUNCATE TABLE T_EVENT")) {
            deletePreparedStatement.execute();
        } catch (final SQLException e) {
            throw new EventStoreException(e);
        }
    }
}
