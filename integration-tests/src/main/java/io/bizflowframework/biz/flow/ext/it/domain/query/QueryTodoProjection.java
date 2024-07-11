package io.bizflowframework.biz.flow.ext.it.domain.query;

import io.bizflowframework.biz.flow.ext.it.domain.TodoId;
import io.bizflowframework.biz.flow.ext.it.domain.TodoStatus;
import io.bizflowframework.biz.flow.ext.it.domain.event.TodoCreated;
import io.bizflowframework.biz.flow.ext.it.domain.event.TodoMarkedAsCompleted;
import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRootIdentifier;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.CreatedAt;
import io.bizflowframework.biz.flow.ext.runtime.usecase.VersionedProjection;

import java.util.Objects;

public final class QueryTodoProjection implements VersionedProjection {

    private final TodoId todoId;
    private final String description;

    private TodoStatus status;
    private final CreatedAt createdAt;
    private AggregateVersion aggregateVersion;

    public QueryTodoProjection(final TodoId todoId,
                               final String description,
                               final TodoStatus status,
                               final CreatedAt createdAt,
                               final AggregateVersion aggregateVersion) {
        this.todoId = Objects.requireNonNull(todoId);
        this.description = Objects.requireNonNull(description);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.aggregateVersion = Objects.requireNonNull(aggregateVersion);
    }

    public QueryTodoProjection(final AggregateRootIdentifier<TodoId> aggregateRootIdentifier,
                               final AggregateVersion aggregateVersion,
                               final CreatedAt createdAt,
                               final TodoCreated payload) {
        this(aggregateRootIdentifier.aggregateId(),
                payload.description(),
                TodoStatus.IN_PROGRESS,
                createdAt,
                aggregateVersion);
    }

    public QueryTodoProjection handle(final TodoMarkedAsCompleted todoMarkedAsCompleted,
                                      final AggregateVersion aggregateVersion) {
        this.status = TodoStatus.COMPLETED;
        this.aggregateVersion = Objects.requireNonNull(aggregateVersion);
        return this;
    }

    public TodoId todoId() {
        return todoId;
    }

    public String description() {
        return description;
    }

    public TodoStatus status() {
        return status;
    }

    public CreatedAt createdAt() {
        return createdAt;
    }

    @Override
    public AggregateId aggregateId() {
        return todoId;
    }

    @Override
    public AggregateVersion aggregateVersion() {
        return aggregateVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryTodoProjection that = (QueryTodoProjection) o;
        return Objects.equals(todoId, that.todoId)
                && Objects.equals(description, that.description)
                && status == that.status
                && Objects.equals(createdAt, that.createdAt)
                && Objects.equals(aggregateVersion, that.aggregateVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId, description, status, createdAt, aggregateVersion);
    }
}
