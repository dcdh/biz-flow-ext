package io.bizflowframework.biz.flow.ext.it.api.query;

import io.bizflowframework.biz.flow.ext.it.QueryTodoProjection;
import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.it.TodoStatus;
import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.CreatedAt;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "T_QUERY_TODO")
public final class QueryTodo extends PanacheEntityBase implements QueryTodoProjection {

    @Id
    private String todoId;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TodoStatus todoStatus;

    @Column(nullable = false)
    private Integer version;

    public QueryTodo() {
    }

    public QueryTodo(final TodoId todoId,
                     final CreatedAt createdAt,
                     final String description,
                     final TodoStatus todoStatus,
                     final Integer version) {
        this.todoId = Objects.requireNonNull(todoId).id();
        this.createdAt = Objects.requireNonNull(createdAt).at();
        this.description = Objects.requireNonNull(description);
        this.todoStatus = Objects.requireNonNull(todoStatus);
        this.version = Objects.requireNonNull(version);
    }

    public TodoId todoId() {
        return new TodoId(todoId);
    }

    public String description() {
        return description;
    }

    @Override
    public TodoStatus status() {
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
    public AggregateId aggregateId() {
        return new TodoId(todoId);
    }

    @Override
    public AggregateVersion aggregateVersion() {
        return new AggregateVersion(version);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryTodo queryTodo = (QueryTodo) o;
        return Objects.equals(todoId, queryTodo.todoId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(todoId);
    }
}