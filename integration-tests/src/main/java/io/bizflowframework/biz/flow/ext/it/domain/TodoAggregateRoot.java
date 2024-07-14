package io.bizflowframework.biz.flow.ext.it.domain;

import io.bizflowframework.biz.flow.ext.it.domain.usecase.CreateNewTodoCommandRequest;
import io.bizflowframework.biz.flow.ext.it.domain.usecase.MarkTodoAsCompletedCommandRequest;
import io.bizflowframework.biz.flow.ext.it.domain.event.TodoCreatedEvent;
import io.bizflowframework.biz.flow.ext.it.domain.event.TodoMarkedAsCompletedEvent;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRoot;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.CreatedAtProvider;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.incrementer.AggregateVersionIncrementer;

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

    public void handle(final CreateNewTodoCommandRequest createNewTodoCommand) {
        this.apply(new TodoCreatedEvent(createNewTodoCommand.description()));
    }

    public void handle(final MarkTodoAsCompletedCommandRequest markTodoAsCompletedCommand)
            throws TodoAlreadyMarkedAsCompletedException {
        if (TodoStatus.COMPLETED.equals(status)) {
            throw new TodoAlreadyMarkedAsCompletedException(markTodoAsCompletedCommand.todoId());
        }
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
}
