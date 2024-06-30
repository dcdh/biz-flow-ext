package io.bizflowframework.biz.flow.ext.runtime.usecase;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;

import java.io.Serializable;

public interface Request<ID extends AggregateId> extends Serializable {
    ID aggregateId();
}
