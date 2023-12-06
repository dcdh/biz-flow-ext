package io.bizflowframework.biz.flow.ext.runtime;

import io.bizflowframework.biz.flow.ext.runtime.serde.MissingSerdeException;

public interface AggregateRootRepository<ID extends AggregateId, T extends AggregateRoot<ID, T>> {
    T save(T aggregateRoot) throws MissingSerdeException, EventStoreException;

    T load(ID aggregateId) throws UnknownAggregateRootException, MissingSerdeException, EventStoreException;

    T load(ID aggregateId, AggregateVersion aggregateVersion)
            throws UnknownAggregateRootException, UnknownAggregateRootAtVersionException, MissingSerdeException, EventStoreException;
}