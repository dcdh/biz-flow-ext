package io.bizflowframework.biz.flow.ext.runtime;

public class TodoAggregate extends AggregateRoot<TodoAggregate> {
    public TodoAggregate(final AggregateId aggregateId,
                         final CreatedAtProvider createdAtProvider) {
        super(aggregateId, createdAtProvider);
    }

    public void on(final TodoCreated todoCreated) {
    }
}
