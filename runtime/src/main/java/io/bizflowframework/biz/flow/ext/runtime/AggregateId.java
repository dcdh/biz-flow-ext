package io.bizflowframework.biz.flow.ext.runtime;

import java.io.Serializable;

public interface AggregateId extends Serializable {
    String id();
}
