package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.BaseAggregateRootRepository;
import io.bizflowframework.biz.flow.ext.runtime.BaseOnSavedEvent;
import io.bizflowframework.biz.flow.ext.runtime.creational.AggregateRootInstanceCreator;
import io.bizflowframework.biz.flow.ext.runtime.event.AggregateRootEventPayload;
import io.bizflowframework.biz.flow.ext.runtime.event.EventRepository;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Singleton;

// TODO generate it !
@Singleton
public final class TodoAggregateRootRepository extends BaseAggregateRootRepository<TodoId, TodoAggregateRoot> {
    public TodoAggregateRootRepository(final EventRepository<TodoId, TodoAggregateRoot> eventRepository,
                                       final AggregateRootInstanceCreator aggregateRootInstanceCreator,
                                       final Instance<BaseOnSavedEvent<TodoId, TodoAggregateRoot, ? extends AggregateRootEventPayload<TodoAggregateRoot>>> onSavedEvent) {
        super(eventRepository, aggregateRootInstanceCreator, onSavedEvent);
    }

    @Override
    protected Class<TodoAggregateRoot> clazz() {
        return TodoAggregateRoot.class;
    }
}
