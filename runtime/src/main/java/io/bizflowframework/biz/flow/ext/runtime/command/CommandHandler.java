package io.bizflowframework.biz.flow.ext.runtime.command;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.AggregateRoot;

public interface CommandHandler<ID extends AggregateId, T extends AggregateRoot<ID, T>, C extends Command<ID>> {
    T execute(C command) throws Throwable;
}
