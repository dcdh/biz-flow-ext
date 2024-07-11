package io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRoot;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRootDomainEvent;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRootIdentifier;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.EventStoreException;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.MissingSerdeException;

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
