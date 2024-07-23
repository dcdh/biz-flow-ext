package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.BaseAggregateRootRepository;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.EventHandler;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.creational.AggregateRootInstanceCreator;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event.AggregateRootEventPayload;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event.EventRepository;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Singleton;

// TODO generate it !
@Singleton
public final class TodoAggregateRootRepository extends BaseAggregateRootRepository<TodoId, TodoAggregateRoot> {
    public TodoAggregateRootRepository(final EventRepository<TodoId, TodoAggregateRoot> eventRepository,
                                       final AggregateRootInstanceCreator aggregateRootInstanceCreator,
                                       final Instance<EventHandler<TodoId, TodoAggregateRoot, ? extends AggregateRootEventPayload<TodoAggregateRoot>>> onSavedEvent) {
        super(eventRepository, aggregateRootInstanceCreator, onSavedEvent);
    }

    @Override
    protected Class<TodoAggregateRoot> clazz() {
        return TodoAggregateRoot.class;
    }
}
