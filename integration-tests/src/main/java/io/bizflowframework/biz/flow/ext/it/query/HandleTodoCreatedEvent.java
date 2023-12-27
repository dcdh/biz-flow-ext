package io.bizflowframework.biz.flow.ext.it.query;

import io.bizflowframework.biz.flow.ext.it.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.it.TodoStatus;
import io.bizflowframework.biz.flow.ext.it.event.TodoCreated;
import io.bizflowframework.biz.flow.ext.runtime.AggregateRootIdentifier;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.BaseOnSavedEvent;
import io.bizflowframework.biz.flow.ext.runtime.CreatedAt;
import jakarta.inject.Singleton;

import java.util.Objects;

@Singleton
public class HandleTodoCreatedEvent extends BaseOnSavedEvent<TodoId, TodoAggregateRoot, TodoCreated> {
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
