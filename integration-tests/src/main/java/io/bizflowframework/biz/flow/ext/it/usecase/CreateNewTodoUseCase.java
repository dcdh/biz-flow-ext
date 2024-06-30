package io.bizflowframework.biz.flow.ext.it.usecase;

import io.bizflowframework.biz.flow.ext.it.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.it.TodoIdGenerator;
import io.bizflowframework.biz.flow.ext.runtime.AggregateRootRepository;
import io.bizflowframework.biz.flow.ext.runtime.CreatedAtProvider;
import io.bizflowframework.biz.flow.ext.runtime.usecase.UseCase;
import io.bizflowframework.biz.flow.ext.runtime.incrementer.AggregateVersionIncrementer;

import java.util.Objects;

public final class CreateNewTodoUseCase implements UseCase<TodoId, TodoAggregateRoot, CreateNewTodoRequest, CreateNewTodoException> {
    private final AggregateRootRepository<TodoId, TodoAggregateRoot> aggregateAggregateRootRepository;
    private final TodoIdGenerator todoIdGenerator;
    private final CreatedAtProvider createdAtProvider;
    private final AggregateVersionIncrementer aggregateVersionIncrementer;

    public CreateNewTodoUseCase(final AggregateRootRepository<TodoId, TodoAggregateRoot> aggregateAggregateRootRepository,
                                final TodoIdGenerator todoIdGenerator,
                                final CreatedAtProvider createdAtProvider,
                                final AggregateVersionIncrementer aggregateVersionIncrementer) {
        this.aggregateAggregateRootRepository = Objects.requireNonNull(aggregateAggregateRootRepository);
        this.todoIdGenerator = Objects.requireNonNull(todoIdGenerator);
        this.createdAtProvider = Objects.requireNonNull(createdAtProvider);
        this.aggregateVersionIncrementer = Objects.requireNonNull(aggregateVersionIncrementer);
    }

    @Override
    public TodoAggregateRoot execute(final CreateNewTodoRequest command) throws CreateNewTodoException {
        final TodoId todoIdGenerated = todoIdGenerator.generate();
        final TodoAggregateRoot newTodo = new TodoAggregateRoot(todoIdGenerated, createdAtProvider, aggregateVersionIncrementer);
        newTodo.handle(command);
        return aggregateAggregateRootRepository.save(newTodo);
    }
}
