package io.bizflowframework.biz.flow.ext.it.command;

import io.bizflowframework.biz.flow.ext.it.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.it.TodoIdGenerator;
import io.bizflowframework.biz.flow.ext.runtime.AggregateRootRepository;
import io.bizflowframework.biz.flow.ext.runtime.CreatedAtProvider;
import io.bizflowframework.biz.flow.ext.runtime.command.CommandHandler;
import io.bizflowframework.biz.flow.ext.runtime.incrementer.AggregateVersionIncrementer;
import jakarta.inject.Singleton;

import java.util.Objects;

@Singleton
public final class CreateNewTodoCommandHandler implements CommandHandler<TodoId, TodoAggregateRoot, CreateNewTodoCommand> {
    private final AggregateRootRepository<TodoId, TodoAggregateRoot> aggregateAggregateRootRepository;
    private final TodoIdGenerator todoIdGenerator;
    private final CreatedAtProvider createdAtProvider;
    private final AggregateVersionIncrementer aggregateVersionIncrementer;

    public CreateNewTodoCommandHandler(final AggregateRootRepository<TodoId, TodoAggregateRoot> aggregateAggregateRootRepository,
                                       final TodoIdGenerator todoIdGenerator,
                                       final CreatedAtProvider createdAtProvider,
                                       final AggregateVersionIncrementer aggregateVersionIncrementer) {
        this.aggregateAggregateRootRepository = Objects.requireNonNull(aggregateAggregateRootRepository);
        this.todoIdGenerator = Objects.requireNonNull(todoIdGenerator);
        this.createdAtProvider = Objects.requireNonNull(createdAtProvider);
        this.aggregateVersionIncrementer = Objects.requireNonNull(aggregateVersionIncrementer);
    }

    @Override
    public TodoAggregateRoot execute(final CreateNewTodoCommand command) throws Throwable {
        final TodoId todoIdGenerated = todoIdGenerator.generate();
        final TodoAggregateRoot newTodo = new TodoAggregateRoot(todoIdGenerated, createdAtProvider, aggregateVersionIncrementer);
        newTodo.handle(command);
        return aggregateAggregateRootRepository.save(newTodo);
    }
}
