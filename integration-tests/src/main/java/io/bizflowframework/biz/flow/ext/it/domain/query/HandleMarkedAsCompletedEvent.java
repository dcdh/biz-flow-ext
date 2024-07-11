package io.bizflowframework.biz.flow.ext.it.domain.query;

import io.bizflowframework.biz.flow.ext.it.domain.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.domain.TodoId;
import io.bizflowframework.biz.flow.ext.it.domain.event.TodoMarkedAsCompleted;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRootIdentifier;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.BaseOnSavedEvent;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.CreatedAt;

import java.util.Objects;

public final class HandleMarkedAsCompletedEvent extends BaseOnSavedEvent<TodoId, TodoAggregateRoot, TodoMarkedAsCompleted> {

    private final QueryTodoProjectionRepository queryTodoProjectionRepository;

    public HandleMarkedAsCompletedEvent(final QueryTodoProjectionRepository queryTodoProjectionRepository) {
        this.queryTodoProjectionRepository = Objects.requireNonNull(queryTodoProjectionRepository);
    }

    @Override
    public void execute(final AggregateRootIdentifier<TodoId> aggregateRootIdentifier,
                        final AggregateVersion aggregateVersion,
                        final CreatedAt createdAt,
                        final TodoMarkedAsCompleted payload) {
        final QueryTodoProjection byId = queryTodoProjectionRepository.findById(aggregateRootIdentifier.aggregateId());
        byId.handle(payload, aggregateVersion);
        queryTodoProjectionRepository.persist(byId);
    }
}
