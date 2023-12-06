package io.bizflowframework.biz.flow.ext.runtime;

import java.util.List;

public interface EventRepository {
    <T extends AggregateRoot<T>> void save(AggregateRootDomainEvent<T> aggregateRootDomainEvent);

    <T extends AggregateRoot<T>> List<AggregateRootDomainEvent<T>> loadOrderByVersionASC(AggregateId aggregateId);

    <T extends AggregateRoot<T>> List<AggregateRootDomainEvent<T>> loadOrderByVersionASC(AggregateId aggregateId, AggregateVersion aggregateVersion);
}
