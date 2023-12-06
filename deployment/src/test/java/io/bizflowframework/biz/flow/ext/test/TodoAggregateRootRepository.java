package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.BaseAggregateRootRepository;
import io.bizflowframework.biz.flow.ext.runtime.creational.AggregateRootInstanceCreator;
import io.bizflowframework.biz.flow.ext.runtime.event.EventRepository;
import jakarta.inject.Singleton;

// TODO generate it !
@Singleton
public final class TodoAggregateRootRepository extends BaseAggregateRootRepository<TodoId, TodoAggregateRoot> {
    public TodoAggregateRootRepository(final EventRepository<TodoId, TodoAggregateRoot> eventRepository,
                                       final AggregateRootInstanceCreator aggregateRootInstanceCreator) {
        super(eventRepository, aggregateRootInstanceCreator);
    }

    @Override
    protected Class<TodoAggregateRoot> clazz() {
        return TodoAggregateRoot.class;
    }
}
