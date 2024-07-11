package io.bizflowframework.biz.flow.ext.runtime.eventsourcing.creational;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRoot;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.CreatedAtProvider;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.incrementer.AggregateVersionIncrementer;
import io.quarkus.arc.DefaultBean;
import jakarta.inject.Singleton;

import java.util.Objects;

@Singleton
@DefaultBean
public final class ReflectionAggregateRootInstanceCreator implements AggregateRootInstanceCreator {
    private final CreatedAtProvider createdAtProvider;
    private final AggregateVersionIncrementer aggregateVersionIncrementer;

    public ReflectionAggregateRootInstanceCreator(final CreatedAtProvider createdAtProvider,
                                                  final AggregateVersionIncrementer aggregateVersionIncrementer) {
        this.createdAtProvider = Objects.requireNonNull(createdAtProvider);
        this.aggregateVersionIncrementer = Objects.requireNonNull(aggregateVersionIncrementer);
    }

    @Override
    public <ID extends AggregateId, T extends AggregateRoot<ID, T>> T createNewInstance(final Class<T> clazz, final ID aggregateId) {
        try {
            return clazz.getDeclaredConstructor(aggregateId.getClass(), CreatedAtProvider.class, AggregateVersionIncrementer.class)
                    .newInstance(aggregateId, createdAtProvider, aggregateVersionIncrementer);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
