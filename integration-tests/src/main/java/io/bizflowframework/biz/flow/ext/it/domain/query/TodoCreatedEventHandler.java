package io.bizflowframework.biz.flow.ext.it.domain.query;

import io.bizflowframework.biz.flow.ext.it.domain.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.domain.TodoId;
import io.bizflowframework.biz.flow.ext.it.domain.event.TodoCreatedEvent;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRootIdentifier;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.EventHandler;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.CreatedAt;

import java.util.Objects;

public final class TodoCreatedEventHandler extends EventHandler<TodoId, TodoAggregateRoot, TodoCreatedEvent> {

    private final QueryTodoProjectionRepository queryTodoProjectionRepository;

    public TodoCreatedEventHandler(final QueryTodoProjectionRepository queryTodoProjectionRepository) {
        this.queryTodoProjectionRepository = Objects.requireNonNull(queryTodoProjectionRepository);
    }

    @Override
    public void handle(final AggregateRootIdentifier<TodoId> aggregateRootIdentifier,
                       final AggregateVersion aggregateVersion,
                       final CreatedAt createdAt,
                       final TodoCreatedEvent payload) {
        queryTodoProjectionRepository.persist(new QueryTodoProjection(
                aggregateRootIdentifier, aggregateVersion, createdAt, payload));
    }
}
