package io.bizflowframework.biz.flow.ext.it.domain.query;

import io.bizflowframework.biz.flow.ext.it.domain.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.domain.TodoId;
import io.bizflowframework.biz.flow.ext.it.domain.event.TodoCreatedEvent;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRootIdentifier;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.BaseOnSavedEvent;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.CreatedAt;

import java.util.Objects;

public final class HandleTodoCreatedEvent extends BaseOnSavedEvent<TodoId, TodoAggregateRoot, TodoCreatedEvent> {

    private final QueryTodoProjectionRepository queryTodoProjectionRepository;

    public HandleTodoCreatedEvent(final QueryTodoProjectionRepository queryTodoProjectionRepository) {
        this.queryTodoProjectionRepository = Objects.requireNonNull(queryTodoProjectionRepository);
    }

    @Override
    public void execute(final AggregateRootIdentifier<TodoId> aggregateRootIdentifier,
                        final AggregateVersion aggregateVersion,
                        final CreatedAt createdAt,
                        final TodoCreatedEvent payload) {
        queryTodoProjectionRepository.persist(new QueryTodoProjection(
                aggregateRootIdentifier, aggregateVersion, createdAt, payload));
    }
}
