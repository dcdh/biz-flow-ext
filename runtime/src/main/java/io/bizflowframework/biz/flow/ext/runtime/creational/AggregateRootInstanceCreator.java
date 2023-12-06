package io.bizflowframework.biz.flow.ext.runtime.creational;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.AggregateRoot;

public interface AggregateRootInstanceCreator {
    <ID extends AggregateId, T extends AggregateRoot<ID, T>> T createNewInstance(Class<T> clazz, ID aggregateId);
}
