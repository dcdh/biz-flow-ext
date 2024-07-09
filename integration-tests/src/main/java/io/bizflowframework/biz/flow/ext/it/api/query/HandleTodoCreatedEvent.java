package io.bizflowframework.biz.flow.ext.it.api.query;

import io.bizflowframework.biz.flow.ext.it.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.it.TodoStatus;
import io.bizflowframework.biz.flow.ext.it.event.TodoCreated;
import io.bizflowframework.biz.flow.ext.runtime.AggregateRootIdentifier;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.BaseOnSavedEvent;
import io.bizflowframework.biz.flow.ext.runtime.CreatedAt;
import jakarta.inject.Singleton;

@Singleton
public class HandleTodoCreatedEvent extends BaseOnSavedEvent<TodoId, TodoAggregateRoot, TodoCreated> {

    @Override
    public void execute(final AggregateRootIdentifier<TodoId> aggregateRootIdentifier,
                        final AggregateVersion aggregateVersion,
                        final CreatedAt createdAt,
                        final TodoCreated payload) {
        QueryTodo.persist(
                new QueryTodo(
                        aggregateRootIdentifier.aggregateId(),
                        createdAt,
                        payload.description(),
                        TodoStatus.IN_PROGRESS,
                        aggregateVersion.version()));
    }
}
