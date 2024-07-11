package io.bizflowframework.biz.flow.ext.it.infra.postgres;

import io.bizflowframework.biz.flow.ext.it.domain.TodoId;
import io.bizflowframework.biz.flow.ext.it.domain.TodoStatus;
import io.bizflowframework.biz.flow.ext.it.domain.query.QueryTodoProjection;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.CreatedAt;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "T_QUERY_TODO")
public final class QueryTodo extends PanacheEntityBase {

    @Id
    private String todoId;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TodoStatus status;

    @Column(nullable = false)
    private Integer version;

    public QueryTodo() {
    }

    public QueryTodo(final QueryTodoProjection queryTodoProjection) {
        this(
                queryTodoProjection.todoId(),
                queryTodoProjection.createdAt(),
                queryTodoProjection.description(),
                queryTodoProjection.status(),
                queryTodoProjection.aggregateVersion().version()
        );
    }

    public QueryTodo(final TodoId todoId,
                     final CreatedAt createdAt,
                     final String description,
                     final TodoStatus status,
                     final Integer version) {
        this.todoId = Objects.requireNonNull(todoId).id();
        this.createdAt = Objects.requireNonNull(createdAt).at();
        this.description = Objects.requireNonNull(description);
        this.status = Objects.requireNonNull(status);
        this.version = Objects.requireNonNull(version);
    }

    public QueryTodoProjection toQueryTodoProjection() {
        return new QueryTodoProjection(
                new TodoId(todoId),
                description,
                status,
                new CreatedAt(createdAt),
                new AggregateVersion(version)
        );
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