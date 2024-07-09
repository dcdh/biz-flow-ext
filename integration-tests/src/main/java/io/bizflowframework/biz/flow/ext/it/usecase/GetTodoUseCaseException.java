package io.bizflowframework.biz.flow.ext.it.usecase;

import io.bizflowframework.biz.flow.ext.it.TodoId;

public final class GetTodoUseCaseException extends Exception {
    public GetTodoUseCaseException(final TodoId todoId) {}
}
