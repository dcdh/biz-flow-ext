package io.bizflowframework.biz.flow.ext.runtime.eventsourcing.creational;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;

public interface AggregateIdInstanceCreator {
    <ID extends AggregateId> ID createInstance(Class<ID> clazz, String aggregateId);
}
