package io.bizflowframework.biz.flow.ext.runtime.usecase;

public interface UseCase<T, R, E extends Exception> {
    T execute(final R request) throws E;
}