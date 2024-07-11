package io.bizflowframework.biz.flow.ext.runtime.eventsourcing.command;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.usecase.CommandRequest;

public interface AggregateCommandRequest<ID extends AggregateId> extends CommandRequest {
    ID aggregateId();
}
