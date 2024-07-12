package io.bizflowframework.biz.flow.ext.it.domain.usecase;

import io.bizflowframework.biz.flow.ext.it.domain.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.domain.TodoAlreadyMarkedAsCompletedException;
import io.bizflowframework.biz.flow.ext.it.domain.TodoId;
import io.bizflowframework.biz.flow.ext.it.domain.UnknownTodoException;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRootRepository;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.UnknownAggregateRootException;
import io.bizflowframework.biz.flow.ext.runtime.usecase.BizMutationUseCase;

import java.util.Objects;

public final class MarkTodoAsCompletedUseCase implements BizMutationUseCase<TodoAggregateRoot, MarkTodoAsCompletedRequest, MarkTodoAsCompletedUseCaseException> {
    private final AggregateRootRepository<TodoId, TodoAggregateRoot> aggregateAggregateRootRepository;

    public MarkTodoAsCompletedUseCase(final AggregateRootRepository<TodoId, TodoAggregateRoot> aggregateAggregateRootRepository) {
        this.aggregateAggregateRootRepository = Objects.requireNonNull(aggregateAggregateRootRepository);
    }

    @Override
    public TodoAggregateRoot execute(final MarkTodoAsCompletedRequest request) throws MarkTodoAsCompletedUseCaseException {
        try {
            final TodoAggregateRoot todoAggregateRoot = aggregateAggregateRootRepository.load(request.aggregateId());
            todoAggregateRoot.handle(request);
            return aggregateAggregateRootRepository.save(todoAggregateRoot);
        } catch (final UnknownAggregateRootException unknownAggregateRootException) {
            throw new MarkTodoAsCompletedUseCaseException(new UnknownTodoException(request.todoId()));
        } catch (final TodoAlreadyMarkedAsCompletedException todoAlreadyMarkedAsCompletedException) {
            throw new MarkTodoAsCompletedUseCaseException(new TodoAlreadyMarkedAsCompletedException(request.todoId()));
        }
    }
}