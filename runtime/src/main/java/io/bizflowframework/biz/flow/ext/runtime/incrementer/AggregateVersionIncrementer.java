package io.bizflowframework.biz.flow.ext.runtime.incrementer;

import io.bizflowframework.biz.flow.ext.runtime.AggregateVersion;

public interface AggregateVersionIncrementer {
    AggregateVersion increment(AggregateVersion aggregateVersion);
}
