package io.bizflowframework.biz.flow.ext.it.infra.api;

import io.bizflowframework.biz.flow.ext.it.domain.query.QueryTodoProjection;
import io.bizflowframework.biz.flow.ext.it.domain.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.domain.TodoId;
import io.bizflowframework.biz.flow.ext.it.domain.usecase.*;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRootRepository;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.CreatedAtProvider;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.EventStoreException;
import io.bizflowframework.biz.flow.ext.runtime.usecase.api.PagingDTO;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.incrementer.AggregateVersionIncrementer;
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
    private final CreateNewTodoBizMutationUseCase createNewTodoBizMutationUseCase;
    private final MarkTodoAsCompletedBizMutationUseCase markTodoAsCompletedBizMutationUseCase;
    private final GetTodoBizQueryUseCase getTodoBizQueryUseCase;
    private final ListTodosBizQueryUseCase listTodosBizQueryUseCase;
    private final AggregateRootRepository<TodoId, TodoAggregateRoot> aggregateAggregateRootRepository;
    private final CreatedAtProvider createdAtProvider;
    private final AggregateVersionIncrementer aggregateVersionIncrementer;
    private final DataSource dataSource;

    public TodoResourceEndpoint(final CreateNewTodoBizMutationUseCase createNewTodoBizMutationUseCase,
                                final MarkTodoAsCompletedBizMutationUseCase markTodoAsCompletedBizMutationUseCase,
                                final GetTodoBizQueryUseCase getTodoBizQueryUseCase,
                                final ListTodosBizQueryUseCase listTodosBizQueryUseCase,
                                final AggregateRootRepository<TodoId, TodoAggregateRoot> todoAggregateRootRepository,
                                final CreatedAtProvider createdAtProvider,
                                final AggregateVersionIncrementer aggregateVersionIncrementer,
                                final DataSource dataSource) {
        this.createNewTodoBizMutationUseCase = Objects.requireNonNull(createNewTodoBizMutationUseCase);
        this.markTodoAsCompletedBizMutationUseCase = Objects.requireNonNull(markTodoAsCompletedBizMutationUseCase);
        this.getTodoBizQueryUseCase = Objects.requireNonNull(getTodoBizQueryUseCase);
        this.listTodosBizQueryUseCase = Objects.requireNonNull(listTodosBizQueryUseCase);
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
    public TodoDTO createNewTodo(@FormParam("description") final String description) throws CreateNewTodoBizMutationUseCaseException {
        final TodoAggregateRoot todoCreated = createNewTodoBizMutationUseCase.execute(new CreateNewTodoCommandRequest(description));
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
    public TodoDTO markTodoAsCompleted(@FormParam("todoId") final String todoId) throws MarkTodoAsCompletedBizMutationUseCaseException {
        final TodoAggregateRoot todoCompleted = markTodoAsCompletedBizMutationUseCase.execute(new MarkTodoAsCompletedCommandRequest(new TodoId(todoId)));
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
    public ListOfTodosDTO listTodos(@BeanParam final PagingDTO pagingDTO) throws ListTodosBizQueryUseCaseException {
        final ListOfProjection<QueryTodoProjection> listOf = listTodosBizQueryUseCase.execute(new ListTodosQueryRequest(pagingDTO.toPaging()));
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
    public TodoDTO getByTodoId(@PathParam("todoId") final TodoId todoId) throws GetTodoBizQueryUseCaseException {
        return new TodoDTO(getTodoBizQueryUseCase.execute(new GetTodoQueryRequest(todoId)));
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
        givenTodoAggregateRoot.handle(new CreateNewTodoCommandRequest("lorem ipsum dolor sit amet"));
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
