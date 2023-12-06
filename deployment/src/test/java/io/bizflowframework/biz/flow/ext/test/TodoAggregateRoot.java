package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.AggregateRoot;
import io.bizflowframework.biz.flow.ext.runtime.CreatedAtProvider;
import io.bizflowframework.biz.flow.ext.runtime.incrementer.AggregateVersionIncrementer;
import io.bizflowframework.biz.flow.ext.test.event.TodoCreated;
import io.bizflowframework.biz.flow.ext.test.event.TodoMarkedAsCompleted;
import io.bizflowframework.biz.flow.ext.test.event.UnknownTodoEvent;

import java.util.Objects;

public final class TodoAggregateRoot extends AggregateRoot<TodoId, TodoAggregateRoot> {
    public static final String DESCRIPTION = "description";
    private String description;
    private TodoStatus status;

    public TodoAggregateRoot(final TodoId aggregateId,
                             final CreatedAtProvider createdAtProvider,
                             final AggregateVersionIncrementer aggregateVersionIncrementer) {
        super(aggregateId, createdAtProvider, aggregateVersionIncrementer);
    }

    public void createNewTodo(final String description) {
        this.apply(new TodoCreated(description));
    }

    public void markTodoAsCompleted() {
        this.apply(new TodoMarkedAsCompleted());
    }

    public void addUnknownTodoEvent() {
        this.apply(new UnknownTodoEvent());
    }

    public void on(final TodoCreated todoCreated) {
        this.description = todoCreated.description();
        this.status = TodoStatus.IN_PROGRESS;
    }

    public void on(final TodoMarkedAsCompleted todoMarkedAsCompleted) {
        this.status = TodoStatus.COMPLETED;
    }

    public String description() {
        return description;
    }

    public TodoStatus status() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TodoAggregateRoot that = (TodoAggregateRoot) o;
        return Objects.equals(description, that.description) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description, status);
    }

    @Override
    public String toString() {
        return "TodoAggregateRoot{" +
               "description='" + description + '\'' +
               ", status=" + status +
               ", aggregateRootIdentifier=" + aggregateRootIdentifier +
               '}';
    }
}
