package io.bizflowframework.biz.flow.ext.it.domain.usecase;

import io.bizflowframework.biz.flow.ext.it.domain.TodoId;

public final class GetTodoUseCaseException extends Exception {
    public GetTodoUseCaseException(final TodoId todoId) {}
}
