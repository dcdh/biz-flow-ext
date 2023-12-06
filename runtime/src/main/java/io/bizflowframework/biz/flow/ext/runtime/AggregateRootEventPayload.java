package io.bizflowframework.biz.flow.ext.runtime;

public interface AggregateRootEventPayload<T extends AggregateRoot<T>> {
    void apply(T aggregateRoot);
}
