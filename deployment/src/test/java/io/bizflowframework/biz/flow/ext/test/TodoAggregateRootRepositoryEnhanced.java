package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.BaseAggregateRootRepository;
import io.bizflowframework.biz.flow.ext.runtime.creational.AggregateRootInstanceCreator;
import io.bizflowframework.biz.flow.ext.runtime.event.EventRepository;
import jakarta.inject.Singleton;

// TODO generate it !
@Singleton
public final class TodoAggregateRootRepositoryEnhanced extends BaseAggregateRootRepository<TodoId, TodoAggregateRoot> {
    public TodoAggregateRootRepositoryEnhanced(final EventRepository<TodoId, TodoAggregateRoot> eventRepository,
                                               final AggregateRootInstanceCreator aggregateRootInstanceCreator) {
        super(eventRepository, aggregateRootInstanceCreator);
    }
}
