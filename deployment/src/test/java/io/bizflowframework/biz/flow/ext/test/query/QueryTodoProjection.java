package io.bizflowframework.biz.flow.ext.test.query;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;
import io.bizflowframework.biz.flow.ext.runtime.usecase.VersionedProjection;

public final class QueryTodoProjection implements VersionedProjection {
    @Override
    public AggregateId aggregateId() {
        throw new IllegalStateException("Should not be called");
    }

    @Override
    public AggregateVersion aggregateVersion() {
        throw new IllegalStateException("Should not be called");
    }
}
