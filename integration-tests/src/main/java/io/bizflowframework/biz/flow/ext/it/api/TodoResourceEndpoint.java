package io.bizflowframework.biz.flow.ext.it.api;

import io.bizflowframework.biz.flow.ext.it.QueryTodoProjection;
import io.bizflowframework.biz.flow.ext.it.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.it.usecase.*;
import io.bizflowframework.biz.flow.ext.runtime.AggregateRootRepository;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.CreatedAtProvider;
import io.bizflowframework.biz.flow.ext.runtime.EventStoreException;
import io.bizflowframework.biz.flow.ext.runtime.api.PagingDTO;
import io.bizflowframework.biz.flow.ext.runtime.incrementer.AggregateVersionIncrementer;
import io.bizflowframework.biz.flow.ext.runtime.usecase.ListOfProjection;
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
    private final CreateNewTodoUseCase createNewTodoUseCase;
    private final MarkTodoAsCompletedUseCase markTodoAsCompletedUseCase;
    private final GetTodoUseCase getTodoUseCase;
    private final ListTodosUseCase listTodosUseCase;
    private final AggregateRootRepository<TodoId, TodoAggregateRoot> aggregateAggregateRootRepository;
    private final CreatedAtProvider createdAtProvider;
    private final AggregateVersionIncrementer aggregateVersionIncrementer;
    private final DataSource dataSource;

    public TodoResourceEndpoint(final CreateNewTodoUseCase createNewTodoUseCase,
                                final MarkTodoAsCompletedUseCase markTodoAsCompletedUseCase,
                                final GetTodoUseCase getTodoUseCase,
                                final ListTodosUseCase listTodosUseCase,
                                final AggregateRootRepository<TodoId, TodoAggregateRoot> todoAggregateRootRepository,
                                final CreatedAtProvider createdAtProvider,
                                final AggregateVersionIncrementer aggregateVersionIncrementer,
                                final DataSource dataSource) {
        this.createNewTodoUseCase = Objects.requireNonNull(createNewTodoUseCase);
        this.markTodoAsCompletedUseCase = Objects.requireNonNull(markTodoAsCompletedUseCase);
        this.getTodoUseCase = Objects.requireNonNull(getTodoUseCase);
        this.listTodosUseCase = Objects.requireNonNull(listTodosUseCase);
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
    public TodoDTO createNewTodo(@FormParam("description") final String description) throws CreateNewTodoUseCaseException {
        final TodoAggregateRoot todoCreated = createNewTodoUseCase.execute(new CreateNewTodoRequest(description));
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
                                                    name = "Todo",
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
    public TodoDTO markTodoAsCompleted(@FormParam("todoId") final String todoId) throws MarkTodoAsCompletedUseCaseException {
        final TodoAggregateRoot todoCompleted = markTodoAsCompletedUseCase.execute(new MarkTodoAsCompletedRequest(new TodoId(todoId)));
        return new TodoDTO(todoCompleted);
    }

    @GET
    @Produces("application/vnd.todos-v1+json")
    @Path("/")
    @RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(
                            type = SchemaType.OBJECT,
                            implementation = PagingDTO.class
                    )
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
                                            implementation = ListOfTodosDTO.class
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "List of todos",
                                                    //language=JSON
                                                    value = """
                                                            {
                                                                "todos": [
                                                                    {
                                                                        "todoId": "7c22337e-06bf-41a1-a876-06e359e9d2af",
                                                                        "description": "lorem ipsum dolor sit amet",
                                                                        "status": "COMPLETED",
                                                                        "version": 1
                                                                    }
                                                                ],
                                                                "nbOfElements": 1
                                                            }
                                                            """
                                            )
                                    }
                            )
                    )
            }
    )
    public ListOfTodosDTO getAll(@BeanParam final PagingDTO pagingDTO) throws ListTodosUseCaseException {
        final ListOfProjection<QueryTodoProjection> listOf = listTodosUseCase.execute(new ListTodosRequest(pagingDTO.toPaging()));
        return new ListOfTodosDTO(
                listOf.projections().stream()
                        .map(TodoDTO::new)
                        .toList(),
                listOf.nbOfElements().nb()
        );
    }

    @GET
    @Produces("application/vnd.todo-v1+json")
    @Path("/{todoId}")
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
                                                    name = "Todo",
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
    public TodoDTO getByTodoId(@PathParam("todoId") final TodoId todoId) throws GetTodoUseCaseException {
        return new TodoDTO(getTodoUseCase.execute(new GetTodoRequest(todoId)));
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
