package io.bizflowframework.biz.flow.ext.runtime;

public interface AggregateRootInstanceCreator {
    <T extends AggregateRoot<T>> T createNewInstance(Class<T> clazz, AggregateId aggregateId);
}
