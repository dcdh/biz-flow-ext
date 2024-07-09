package io.bizflowframework.biz.flow.ext.it.api.query;

import io.bizflowframework.biz.flow.ext.it.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.it.event.TodoMarkedAsCompleted;
import io.bizflowframework.biz.flow.ext.runtime.AggregateRootIdentifier;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.BaseOnSavedEvent;
import io.bizflowframework.biz.flow.ext.runtime.CreatedAt;
import jakarta.inject.Singleton;

@Singleton
public class HandleMarkedAsCompletedEvent extends BaseOnSavedEvent<TodoId, TodoAggregateRoot, TodoMarkedAsCompleted> {

    @Override
    public void execute(final AggregateRootIdentifier<TodoId> aggregateRootIdentifier,
                        final AggregateVersion aggregateVersion,
                        final CreatedAt createdAt,
                        final TodoMarkedAsCompleted payload) {
        final QueryTodo queryTodo = QueryTodo.findById(aggregateRootIdentifier.aggregateId().id());
        queryTodo.markAsCompleted(aggregateVersion.version());
        QueryTodo.persist(queryTodo);
    }
}
