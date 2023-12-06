package io.bizflowframework.biz.flow.ext.runtime;

import java.io.Serializable;
import java.util.Objects;

public record AggregateRootIdentifier<ID extends AggregateId>(ID aggregateId,
                                                              AggregateType aggregateType) implements Serializable {
    public AggregateRootIdentifier {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(aggregateType);
    }
}
