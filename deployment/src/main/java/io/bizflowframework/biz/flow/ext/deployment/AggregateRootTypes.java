package io.bizflowframework.biz.flow.ext.deployment;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRoot;

import java.util.Objects;

public record AggregateRootTypes(Class<?> aggregateRootClass, Class<?> aggregateIdClass) {
    public AggregateRootTypes {
        Objects.requireNonNull(aggregateRootClass);
        assert AggregateRoot.class.isAssignableFrom(aggregateRootClass);
        Objects.requireNonNull(aggregateIdClass);
        assert AggregateId.class.isAssignableFrom(aggregateIdClass);
    }
}
