package io.bizflowframework.biz.flow.ext.it.domain.usecase;

import io.bizflowframework.biz.flow.ext.it.domain.TodoId;

public final class GetTodoBizQueryUseCaseException extends Exception {
    public GetTodoBizQueryUseCaseException(final TodoId todoId) {}
}
