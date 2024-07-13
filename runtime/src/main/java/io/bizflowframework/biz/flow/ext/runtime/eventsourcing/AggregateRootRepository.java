package io.bizflowframework.biz.flow.ext.runtime.eventsourcing;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;

public interface AggregateRootRepository<ID extends AggregateId, T extends AggregateRoot<ID, T>> {
    T save(T aggregateRoot) throws EventStoreException;

    T load(ID aggregateId) throws UnknownAggregateRootException, EventStoreException;

    T load(ID aggregateId, AggregateVersion aggregateVersion)
            throws UnknownAggregateRootException, UnknownAggregateRootAtVersionException, EventStoreException;
}