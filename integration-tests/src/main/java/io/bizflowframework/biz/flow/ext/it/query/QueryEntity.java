package io.bizflowframework.biz.flow.ext.it.query;

import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.it.TodoStatus;

import java.util.Objects;

public final class QueryEntity {
    private final TodoId todoId;
    private final String description;
    private TodoStatus todoStatus;
    private Integer version;

    public QueryEntity(final TodoId todoId,
                       final String description,
                       final TodoStatus todoStatus,
                       final Integer version) {
        this.todoId = Objects.requireNonNull(todoId);
        this.description = Objects.requireNonNull(description);
        this.todoStatus = Objects.requireNonNull(todoStatus);
        this.version = Objects.requireNonNull(version);
    }

    public TodoId todoId() {
        return todoId;
    }

    public String description() {
        return description;
    }

    public TodoStatus todoStatus() {
        return todoStatus;
    }

    public Integer version() {
        return version;
    }

    public void markAsCompleted(final Integer version) {
        this.todoStatus = TodoStatus.COMPLETED;
        this.version = Objects.requireNonNull(version);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryEntity that = (QueryEntity) o;
        return Objects.equals(todoId, that.todoId)
               && Objects.equals(description, that.description)
               && todoStatus == that.todoStatus
               && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId, description, todoStatus, version);
    }
}