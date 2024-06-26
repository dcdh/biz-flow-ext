package io.bizflowframework.biz.flow.ext.it.usecase;

import io.bizflowframework.biz.flow.ext.it.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.TodoAlreadyMarkedAsCompletedException;
import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.it.UnknownTodoException;
import io.bizflowframework.biz.flow.ext.runtime.AggregateRootRepository;
import io.bizflowframework.biz.flow.ext.runtime.UnknownAggregateRootException;
import io.bizflowframework.biz.flow.ext.runtime.usecase.UseCase;

import java.util.Objects;

public final class MarkTodoAsCompletedUseCase implements UseCase<TodoId, TodoAggregateRoot, MarkTodoAsCompletedRequest, MarkTodoAsCompletedException> {
    private final AggregateRootRepository<TodoId, TodoAggregateRoot> aggregateAggregateRootRepository;

    public MarkTodoAsCompletedUseCase(final AggregateRootRepository<TodoId, TodoAggregateRoot> aggregateAggregateRootRepository) {
        this.aggregateAggregateRootRepository = Objects.requireNonNull(aggregateAggregateRootRepository);
    }

    @Override
    public TodoAggregateRoot execute(final MarkTodoAsCompletedRequest request) throws MarkTodoAsCompletedException {
        try {
            final TodoAggregateRoot todoAggregateRoot = aggregateAggregateRootRepository.load(request.aggregateId());
            todoAggregateRoot.handle(request);
            return aggregateAggregateRootRepository.save(todoAggregateRoot);
        } catch (final UnknownAggregateRootException unknownAggregateRootException) {
            throw new MarkTodoAsCompletedException(new UnknownTodoException(request.todoId()));
        } catch (final TodoAlreadyMarkedAsCompletedException todoAlreadyMarkedAsCompletedException) {
            throw new MarkTodoAsCompletedException(new TodoAlreadyMarkedAsCompletedException(request.todoId()));
        }
    }
}
