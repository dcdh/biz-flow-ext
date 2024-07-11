package io.bizflowframework.biz.flow.ext.it.domain.usecase;

public final class MarkTodoAsCompletedUseCaseException extends RuntimeException {
    public MarkTodoAsCompletedUseCaseException(final Exception cause) {
        super(cause);
    }
}
