package io.bizflowframework.biz.flow.ext.it.domain.usecase;

import io.bizflowframework.biz.flow.ext.it.domain.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.domain.TodoId;
import io.bizflowframework.biz.flow.ext.it.domain.TodoIdGenerator;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRootRepository;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.CreatedAtProvider;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.incrementer.AggregateVersionIncrementer;
import io.bizflowframework.biz.flow.ext.runtime.usecase.BizMutationUseCase;

import java.util.Objects;

public final class CreateNewTodoBizMutationUseCase implements BizMutationUseCase<TodoAggregateRoot, CreateNewTodoCommandRequest, CreateNewTodoBizMutationUseCaseException> {
    private final AggregateRootRepository<TodoId, TodoAggregateRoot> aggregateAggregateRootRepository;
    private final TodoIdGenerator todoIdGenerator;
    private final CreatedAtProvider createdAtProvider;
    private final AggregateVersionIncrementer aggregateVersionIncrementer;

    public CreateNewTodoBizMutationUseCase(final AggregateRootRepository<TodoId, TodoAggregateRoot> aggregateAggregateRootRepository,
                                           final TodoIdGenerator todoIdGenerator,
                                           final CreatedAtProvider createdAtProvider,
                                           final AggregateVersionIncrementer aggregateVersionIncrementer) {
        this.aggregateAggregateRootRepository = Objects.requireNonNull(aggregateAggregateRootRepository);
        this.todoIdGenerator = Objects.requireNonNull(todoIdGenerator);
        this.createdAtProvider = Objects.requireNonNull(createdAtProvider);
        this.aggregateVersionIncrementer = Objects.requireNonNull(aggregateVersionIncrementer);
    }

    @Override
    public TodoAggregateRoot execute(final CreateNewTodoCommandRequest command) throws CreateNewTodoBizMutationUseCaseException {
        final TodoId todoIdGenerated = todoIdGenerator.generate();
        final TodoAggregateRoot newTodo = new TodoAggregateRoot(todoIdGenerated, createdAtProvider, aggregateVersionIncrementer);
        newTodo.handle(command);
        return aggregateAggregateRootRepository.save(newTodo);
    }
}
