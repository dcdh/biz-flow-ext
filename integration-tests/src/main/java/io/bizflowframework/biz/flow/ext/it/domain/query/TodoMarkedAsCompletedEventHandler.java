package io.bizflowframework.biz.flow.ext.it.domain.query;

import io.bizflowframework.biz.flow.ext.it.domain.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.domain.TodoId;
import io.bizflowframework.biz.flow.ext.it.domain.event.TodoMarkedAsCompletedEvent;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRootIdentifier;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.EventHandler;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.CreatedAt;

import java.util.Objects;

public final class TodoMarkedAsCompletedEventHandler extends EventHandler<TodoId, TodoAggregateRoot, TodoMarkedAsCompletedEvent> {

    private final QueryTodoProjectionRepository queryTodoProjectionRepository;

    public TodoMarkedAsCompletedEventHandler(final QueryTodoProjectionRepository queryTodoProjectionRepository) {
        this.queryTodoProjectionRepository = Objects.requireNonNull(queryTodoProjectionRepository);
    }

    @Override
    public void handle(final AggregateRootIdentifier<TodoId> aggregateRootIdentifier,
                       final AggregateVersion aggregateVersion,
                       final CreatedAt createdAt,
                       final TodoMarkedAsCompletedEvent payload) {
        final QueryTodoProjection byId = queryTodoProjectionRepository.findById(aggregateRootIdentifier.aggregateId());
        byId.handle(payload, aggregateVersion);
        queryTodoProjectionRepository.persist(byId);
    }
}
