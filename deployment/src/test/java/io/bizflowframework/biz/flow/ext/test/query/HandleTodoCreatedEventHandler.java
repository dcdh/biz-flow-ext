package io.bizflowframework.biz.flow.ext.test.query;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRootIdentifier;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.CreatedAt;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.EventHandler;
import io.bizflowframework.biz.flow.ext.test.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.test.TodoId;
import io.bizflowframework.biz.flow.ext.test.TodoStatus;
import io.bizflowframework.biz.flow.ext.test.event.TodoCreatedEvent;

import java.util.Objects;

public final class HandleTodoCreatedEventHandler extends EventHandler<TodoId, TodoAggregateRoot, TodoCreatedEvent> {
    private final QueryService queryService;

    public HandleTodoCreatedEventHandler(final QueryService queryService) {
        this.queryService = Objects.requireNonNull(queryService);
    }

    @Override
    public void handle(final AggregateRootIdentifier<TodoId> aggregateRootIdentifier,
                       final AggregateVersion aggregateVersion,
                       final CreatedAt createdAt,
                       final TodoCreatedEvent payload) {
        this.queryService.store(new QueryEntity(aggregateRootIdentifier.aggregateId(),
                payload.description(),
                TodoStatus.IN_PROGRESS,
                aggregateVersion.version()));
    }
}
