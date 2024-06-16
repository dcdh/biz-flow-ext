package io.bizflowframework.biz.flow.ext.it.command;

import io.bizflowframework.biz.flow.ext.it.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.it.UnknownTodoException;
import io.bizflowframework.biz.flow.ext.runtime.AggregateRootRepository;
import io.bizflowframework.biz.flow.ext.runtime.UnknownAggregateRootException;
import io.bizflowframework.biz.flow.ext.runtime.command.CommandHandler;

import java.util.Objects;

public final class MarkTodoAsCompletedCommandHandler implements CommandHandler<TodoId, TodoAggregateRoot, MarkTodoAsCompletedCommand> {
    private final AggregateRootRepository<TodoId, TodoAggregateRoot> aggregateAggregateRootRepository;

    public MarkTodoAsCompletedCommandHandler(final AggregateRootRepository<TodoId, TodoAggregateRoot> aggregateAggregateRootRepository) {
        this.aggregateAggregateRootRepository = Objects.requireNonNull(aggregateAggregateRootRepository);
    }

    @Override
    public TodoAggregateRoot execute(final MarkTodoAsCompletedCommand command) throws Throwable {
        try {
            final TodoAggregateRoot todoAggregateRoot = aggregateAggregateRootRepository.load(command.aggregateId());
            todoAggregateRoot.handle(command);
            return aggregateAggregateRootRepository.save(todoAggregateRoot);
        } catch (final UnknownAggregateRootException unknownAggregateRootException) {
            throw new UnknownTodoException(command.todoId());
        }
    }
}
