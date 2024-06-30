package io.bizflowframework.biz.flow.ext.it.usecase;

public final class MarkTodoAsCompletedException extends RuntimeException {
    public MarkTodoAsCompletedException(final Exception cause) {
        super(cause);
    }
}
