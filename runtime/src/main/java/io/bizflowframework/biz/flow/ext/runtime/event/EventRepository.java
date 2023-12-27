package io.bizflowframework.biz.flow.ext.runtime.event;

import io.bizflowframework.biz.flow.ext.runtime.*;
import io.bizflowframework.biz.flow.ext.runtime.serde.MissingSerdeException;

import java.util.List;

public interface EventRepository<ID extends AggregateId, T extends AggregateRoot<ID, T>> {
    void save(AggregateRootDomainEvent<ID, T, ? extends AggregateRootEventPayload<T>> aggregateRootDomainEvent) throws MissingSerdeException, EventStoreException;

    // Issue while returning List of AggregateRootDomainEvent<ID, T> by consumers
    List<AggregateRootDomainEvent> loadOrderByVersionASC(AggregateRootIdentifier<ID> aggregateRootIdentifier)
            throws MissingSerdeException, EventStoreException;

    // Issue while returning List of AggregateRootDomainEvent<ID, T> by consumers
    List<AggregateRootDomainEvent> loadHavingMaxVersionOrderByVersionASC(AggregateRootIdentifier<ID> aggregateRootIdentifier, AggregateVersion aggregateVersion)
            throws MissingSerdeException, EventStoreException;
}
