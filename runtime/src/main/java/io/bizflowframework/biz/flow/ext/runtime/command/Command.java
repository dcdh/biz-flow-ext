package io.bizflowframework.biz.flow.ext.runtime.command;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;

import java.io.Serializable;

public interface Command<ID extends AggregateId> extends Serializable {
    ID aggregateId();
}
