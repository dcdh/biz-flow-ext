package io.bizflowframework.biz.flow.ext.runtime;

import jakarta.inject.Singleton;

import java.util.Objects;

@Singleton
public final class DefaultAggregateRootInstanceCreator implements AggregateRootInstanceCreator {
    private final CreatedAtProvider createdAtProvider;

    public DefaultAggregateRootInstanceCreator(final CreatedAtProvider createdAtProvider) {
        this.createdAtProvider = Objects.requireNonNull(createdAtProvider);
    }

    @Override
    public <T extends AggregateRoot<T>> T createNewInstance(final Class<T> clazz, final AggregateId aggregateId) {
        try {
            return clazz.getDeclaredConstructor(AggregateId.class, CreatedAtProvider.class)
                    .newInstance(aggregateId, createdAtProvider);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
