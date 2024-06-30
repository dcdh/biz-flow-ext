package io.bizflowframework.biz.flow.ext.runtime.usecase;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.AggregateRoot;

public interface UseCase<ID extends AggregateId, T extends AggregateRoot<ID, T>, R extends Request<ID>, E extends RuntimeException> {
    T execute(R request) throws E;
}
