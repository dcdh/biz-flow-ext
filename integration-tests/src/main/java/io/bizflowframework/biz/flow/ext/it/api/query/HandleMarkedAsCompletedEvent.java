package io.bizflowframework.biz.flow.ext.it.api.query;

import io.bizflowframework.biz.flow.ext.it.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.it.event.TodoMarkedAsCompleted;
import io.bizflowframework.biz.flow.ext.runtime.AggregateRootIdentifier;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.BaseOnSavedEvent;
import io.bizflowframework.biz.flow.ext.runtime.CreatedAt;
import jakarta.inject.Singleton;

import java.util.Objects;

@Singleton
public class HandleMarkedAsCompletedEvent extends BaseOnSavedEvent<TodoId, TodoAggregateRoot, TodoMarkedAsCompleted> {
    private final QueryService queryService;

    public HandleMarkedAsCompletedEvent(final QueryService queryService) {
        this.queryService = Objects.requireNonNull(queryService);
    }

    @Override
    public void execute(final AggregateRootIdentifier<TodoId> aggregateRootIdentifier,
                        final AggregateVersion aggregateVersion,
                        final CreatedAt createdAt,
                        final TodoMarkedAsCompleted payload) {
        final QueryEntity queryEntity = this.queryService.getByTodoId(aggregateRootIdentifier.aggregateId());
        queryEntity.markAsCompleted(aggregateVersion.version());
    }
}
