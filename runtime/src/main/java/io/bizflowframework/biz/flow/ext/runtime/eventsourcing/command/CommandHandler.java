package io.bizflowframework.biz.flow.ext.runtime.eventsourcing.command;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRoot;

public interface CommandHandler<ID extends AggregateId, T extends AggregateRoot<ID, T>, R extends AggregateCommandRequest<ID>, E extends Exception> {
    T execute(R request) throws E;
}
