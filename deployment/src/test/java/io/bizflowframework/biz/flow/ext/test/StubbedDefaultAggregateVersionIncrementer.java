package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.incrementer.AggregateVersionIncrementer;
import io.bizflowframework.biz.flow.ext.runtime.incrementer.DefaultAggregateVersionIncrementer;
import jakarta.inject.Singleton;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Singleton
public final class StubbedDefaultAggregateVersionIncrementer implements AggregateVersionIncrementer {
    private final Queue<AggregateVersion> stubbedResponses = new ConcurrentLinkedQueue<>();
    private final DefaultAggregateVersionIncrementer defaultAggregateVersionIncrementer;

    StubbedDefaultAggregateVersionIncrementer(final DefaultAggregateVersionIncrementer defaultAggregateVersionIncrementer) {
        this.defaultAggregateVersionIncrementer = Objects.requireNonNull(defaultAggregateVersionIncrementer);
    }

    public StubbedDefaultAggregateVersionIncrementer addResponse(final AggregateVersion response) {
        this.stubbedResponses.add(response);
        return this;
    }

    @Override
    public AggregateVersion increment(final AggregateVersion aggregateVersion) {
        if (stubbedResponses.isEmpty()) {
            return this.defaultAggregateVersionIncrementer.increment(aggregateVersion);
        }
        return stubbedResponses.poll();
    }
}