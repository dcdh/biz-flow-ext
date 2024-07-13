package io.bizflowframework.biz.flow.ext.test.usecase;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.CreatedAtProvider;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.incrementer.AggregateVersionIncrementer;
import io.bizflowframework.biz.flow.ext.runtime.usecase.BizMutationUseCase;
import io.bizflowframework.biz.flow.ext.test.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.test.TodoId;

import java.util.Objects;

public final class CreateTodoBizMutationUseCase implements BizMutationUseCase<TodoAggregateRoot, CreateTodoCommandRequest, CreateTodoBizMutationUseCaseException> {

    private final CreatedAtProvider createdAtProvider;
    private final AggregateVersionIncrementer aggregateVersionIncrementer;

    public CreateTodoBizMutationUseCase(final CreatedAtProvider createdAtProvider,
                                        final AggregateVersionIncrementer aggregateVersionIncrementer) {
        this.createdAtProvider = Objects.requireNonNull(createdAtProvider);
        this.aggregateVersionIncrementer = Objects.requireNonNull(aggregateVersionIncrementer);
    }

    @Override
    public TodoAggregateRoot execute(final CreateTodoCommandRequest request) throws CreateTodoBizMutationUseCaseException {
        return new TodoAggregateRoot(new TodoId("todoId"),
                createdAtProvider, aggregateVersionIncrementer);
    }
}
