package io.bizflowframework.biz.flow.ext.it.domain.usecase;

import io.bizflowframework.biz.flow.ext.it.domain.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.domain.TodoAlreadyMarkedAsCompletedException;
import io.bizflowframework.biz.flow.ext.it.domain.TodoId;
import io.bizflowframework.biz.flow.ext.it.domain.UnknownTodoException;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRootRepository;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.UnknownAggregateRootException;
import io.bizflowframework.biz.flow.ext.runtime.usecase.BizMutationUseCase;

import java.util.Objects;

public final class MarkTodoAsCompletedBizMutationUseCase implements BizMutationUseCase<TodoAggregateRoot, MarkTodoAsCompletedCommandRequest, MarkTodoAsCompletedBizMutationUseCaseException> {
    private final AggregateRootRepository<TodoId, TodoAggregateRoot> aggregateAggregateRootRepository;

    public MarkTodoAsCompletedBizMutationUseCase(final AggregateRootRepository<TodoId, TodoAggregateRoot> aggregateAggregateRootRepository) {
        this.aggregateAggregateRootRepository = Objects.requireNonNull(aggregateAggregateRootRepository);
    }

    @Override
    public TodoAggregateRoot execute(final MarkTodoAsCompletedCommandRequest request) throws MarkTodoAsCompletedBizMutationUseCaseException {
        try {
            final TodoAggregateRoot todoAggregateRoot = aggregateAggregateRootRepository.load(request.aggregateId());
            todoAggregateRoot.handle(request);
            return aggregateAggregateRootRepository.save(todoAggregateRoot);
        } catch (final UnknownAggregateRootException unknownAggregateRootException) {
            throw new MarkTodoAsCompletedBizMutationUseCaseException(new UnknownTodoException(request.todoId()));
        } catch (final TodoAlreadyMarkedAsCompletedException todoAlreadyMarkedAsCompletedException) {
            throw new MarkTodoAsCompletedBizMutationUseCaseException(new TodoAlreadyMarkedAsCompletedException(request.todoId()));
        }
    }
}
