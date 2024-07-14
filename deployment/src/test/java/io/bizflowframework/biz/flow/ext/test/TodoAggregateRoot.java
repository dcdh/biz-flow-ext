package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRoot;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.CreatedAtProvider;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.incrementer.AggregateVersionIncrementer;
import io.bizflowframework.biz.flow.ext.test.event.TodoCreatedEvent;
import io.bizflowframework.biz.flow.ext.test.event.TodoMarkedAsCompletedEvent;

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
        this.apply(new TodoCreatedEvent(description));
    }

    public void markTodoAsCompleted() {
        this.apply(new TodoMarkedAsCompletedEvent());
    }

    public void on(final TodoCreatedEvent todoCreatedEvent) {
        this.description = todoCreatedEvent.description();
        this.status = TodoStatus.IN_PROGRESS;
    }

    public void on(final TodoMarkedAsCompletedEvent todoMarkedAsCompletedEvent) {
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
