package io.bizflowframework.biz.flow.ext.it.api;

import io.agroal.api.AgroalDataSource;
import io.bizflowframework.biz.flow.ext.it.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.it.command.CreateNewTodoCommand;
import io.bizflowframework.biz.flow.ext.it.command.CreateNewTodoCommandHandler;
import io.bizflowframework.biz.flow.ext.it.command.MarkTodoAsCompletedCommand;
import io.bizflowframework.biz.flow.ext.it.command.MarkTodoAsCompletedCommandHandler;
import io.bizflowframework.biz.flow.ext.runtime.AggregateRootRepository;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.CreatedAtProvider;
import io.bizflowframework.biz.flow.ext.runtime.EventStoreException;
import io.bizflowframework.biz.flow.ext.runtime.incrementer.AggregateVersionIncrementer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

@Path("/todo")
@ApplicationScoped
public class TodoResource {
    private final CreateNewTodoCommandHandler createNewTodoCommandHandler;
    private final MarkTodoAsCompletedCommandHandler markTodoAsCompletedCommandHandler;
    private final AggregateRootRepository<TodoId, TodoAggregateRoot> aggregateAggregateRootRepository;
    private final CreatedAtProvider createdAtProvider;
    private final AggregateVersionIncrementer aggregateVersionIncrementer;
    private final AgroalDataSource dataSource;

    public TodoResource(final CreateNewTodoCommandHandler createNewTodoCommandHandler,
                        final MarkTodoAsCompletedCommandHandler markTodoAsCompletedCommandHandler,
                        final AggregateRootRepository<TodoId, TodoAggregateRoot> aggregateAggregateRootRepository,
                        final CreatedAtProvider createdAtProvider,
                        final AggregateVersionIncrementer aggregateVersionIncrementer,
                        final AgroalDataSource dataSource) {
        this.createNewTodoCommandHandler = Objects.requireNonNull(createNewTodoCommandHandler);
        this.markTodoAsCompletedCommandHandler = Objects.requireNonNull(markTodoAsCompletedCommandHandler);
        this.aggregateAggregateRootRepository = Objects.requireNonNull(aggregateAggregateRootRepository);
        this.createdAtProvider = Objects.requireNonNull(createdAtProvider);
        this.aggregateVersionIncrementer = Objects.requireNonNull(aggregateVersionIncrementer);
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("application/vnd.todo-v1+json")
    @Path("/createNewTodo")
    public TodoDTO createNewTodo(@FormParam("description") final String description) throws Throwable {
        final TodoAggregateRoot todoCreated = createNewTodoCommandHandler.execute(new CreateNewTodoCommand(description));
        return new TodoDTO(todoCreated);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces("application/vnd.todo-v1+json")
    @Path("/markTodoAsCompleted")
    public TodoDTO markTodoAsCompleted(@FormParam("todoId") final String todoId) throws Throwable {
        final TodoAggregateRoot todoCompleted = markTodoAsCompletedCommandHandler.execute(new MarkTodoAsCompletedCommand(new TodoId(todoId)));
        return new TodoDTO(todoCompleted);
    }

    @GET
    @Path("/failMissingSerde")
    public void failMissingSerde() {
        final TodoAggregateRoot givenTodoAggregateRoot = new TodoAggregateRoot(
                new TodoId("nice_todo"), createdAtProvider, aggregateVersionIncrementer);
        givenTodoAggregateRoot.addUnknownTodoEvent();
        aggregateAggregateRootRepository.save(givenTodoAggregateRoot);
    }

    @GET
    @Path("/failUnknownAggregate")
    public void failUnknownAggregate() {
        aggregateAggregateRootRepository.load(new TodoId("unknown"));
    }

    @GET
    @Path("/failUnknownAggregateAtVersion")
    public void failUnknownAggregateAtVersion() {
        final AggregateVersion givenUnknownAggregateVersion = new AggregateVersion(10);
        final TodoAggregateRoot givenTodoAggregateRoot = new TodoAggregateRoot(
                new TodoId("todo_to_fail_at_unknown_version"), createdAtProvider, aggregateVersionIncrementer);
        givenTodoAggregateRoot.handle(new CreateNewTodoCommand("lorem ipsum dolor sit amet"));
        aggregateAggregateRootRepository.save(givenTodoAggregateRoot);
        aggregateAggregateRootRepository.load(new TodoId("todo_to_fail_at_unknown_version"), givenUnknownAggregateVersion);
    }

    @GET
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
