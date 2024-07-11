package io.bizflowframework.biz.flow.ext.runtime.eventsourcing.incrementer;

import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.quarkus.arc.DefaultBean;
import jakarta.inject.Singleton;

import java.util.Objects;

@Singleton
@DefaultBean
public final class DefaultAggregateVersionIncrementer implements AggregateVersionIncrementer {
    @Override
    public AggregateVersion increment(final AggregateVersion aggregateVersion) {
        return Objects.requireNonNull(aggregateVersion).increment();
    }
}
