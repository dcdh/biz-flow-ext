package io.bizflowframework.biz.flow.ext.runtime.usecase;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;

public interface VersionedProjection extends Projection {
    AggregateId aggregateId();

    AggregateVersion aggregateVersion();
}
