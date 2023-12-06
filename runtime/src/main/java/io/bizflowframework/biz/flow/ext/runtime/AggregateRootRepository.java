package io.bizflowframework.biz.flow.ext.runtime;

public interface AggregateRootRepository {
    <T extends AggregateRoot<T>> T save(T aggregateRoot);
    <T extends AggregateRoot<T>> T load(AggregateId aggregateId, Class<T> clazz) throws UnknownAggregateRootException;
    <T extends AggregateRoot<T>> T load(AggregateId aggregateId, Class<T> clazz, AggregateVersion aggregateVersion)
            throws UnknownAggregateRootException, UnknownAggregateRootAtVersionException;
}