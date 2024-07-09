package io.bizflowframework.biz.flow.ext.runtime.usecase;

public interface BizQueryUseCase<T extends Projection, R extends QueryRequest, E extends Exception> extends UseCase<T, R, E> {
}
