package io.bizflowframework.biz.flow.ext.runtime;

import java.io.Serializable;
import java.util.Objects;

public record AggregateType(String type) implements Serializable {
    public AggregateType(final String type) {
        this.type = Objects.requireNonNull(type);
    }

    public AggregateType(final AggregateRoot<?, ?> aggregateRoot) {
        this(aggregateRoot.getClass().getSimpleName());
    }

    public AggregateType(final Class<? extends AggregateRoot<?, ?>> aggregateRootClass) {
        this(aggregateRootClass.getSimpleName());
    }
}
