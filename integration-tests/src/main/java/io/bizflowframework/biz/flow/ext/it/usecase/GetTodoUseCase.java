package io.bizflowframework.biz.flow.ext.it.usecase;

import io.bizflowframework.biz.flow.ext.it.QueryTodoProjection;
import io.bizflowframework.biz.flow.ext.it.QueryTodoProjectionRepository;
import io.bizflowframework.biz.flow.ext.runtime.usecase.BizQueryUseCase;

import java.util.Objects;

public final class GetTodoUseCase implements BizQueryUseCase<QueryTodoProjection, GetTodoRequest, GetTodoUseCaseException> {

    private final QueryTodoProjectionRepository queryTodoProjectionRepository;

    public GetTodoUseCase(final QueryTodoProjectionRepository queryTodoProjectionRepository) {
        this.queryTodoProjectionRepository = Objects.requireNonNull(queryTodoProjectionRepository);
    }

    @Override
    public QueryTodoProjection execute(final GetTodoRequest request) throws GetTodoUseCaseException {
        return queryTodoProjectionRepository.findById(request.todoId());
    }
}
