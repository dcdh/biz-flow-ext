package io.bizflowframework.biz.flow.ext.runtime.eventsourcing.creational;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.quarkus.arc.DefaultBean;
import jakarta.inject.Singleton;

@Singleton
@DefaultBean
public final class ReflectionAggregateIdInstanceCreator implements AggregateIdInstanceCreator {
    @Override
    public <ID extends AggregateId> ID createInstance(final Class<ID> clazz, final String aggregateId) {
        try {
            return clazz.getDeclaredConstructor(String.class).newInstance(aggregateId);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
