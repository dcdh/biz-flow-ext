package io.bizflowframework.biz.flow.ext.test.query;

import io.bizflowframework.biz.flow.ext.runtime.AggregateRootIdentifier;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.CreatedAt;
import io.bizflowframework.biz.flow.ext.runtime.BaseOnSavedEvent;
import io.bizflowframework.biz.flow.ext.test.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.test.TodoId;
import io.bizflowframework.biz.flow.ext.test.TodoStatus;
import io.bizflowframework.biz.flow.ext.test.event.TodoCreated;
import jakarta.inject.Singleton;

import java.util.Objects;

@Singleton
public final class HandleTodoCreatedEvent extends BaseOnSavedEvent<TodoId, TodoAggregateRoot, TodoCreated> {
    private final QueryService queryService;

    public HandleTodoCreatedEvent(final QueryService queryService) {
        this.queryService = Objects.requireNonNull(queryService);
    }

    @Override
    public void execute(final AggregateRootIdentifier<TodoId> aggregateRootIdentifier,
                        final AggregateVersion aggregateVersion,
                        final CreatedAt createdAt,
                        final TodoCreated payload) {
        this.queryService.store(new QueryEntity(aggregateRootIdentifier.aggregateId(),
                payload.description(),
                TodoStatus.IN_PROGRESS,
                aggregateVersion.version()));
    }
}
