package io.bizflowframework.biz.flow.ext.it.domain.usecase;

import io.bizflowframework.biz.flow.ext.it.domain.query.QueryTodoProjection;
import io.bizflowframework.biz.flow.ext.it.domain.query.QueryTodoProjectionRepository;
import io.bizflowframework.biz.flow.ext.runtime.usecase.BizQueryUseCase;

import java.util.Objects;

public final class GetTodoBizQueryUseCase implements BizQueryUseCase<QueryTodoProjection, GetTodoQueryRequest, GetTodoBizQueryUseCaseException> {

    private final QueryTodoProjectionRepository queryTodoProjectionRepository;

    public GetTodoBizQueryUseCase(final QueryTodoProjectionRepository queryTodoProjectionRepository) {
        this.queryTodoProjectionRepository = Objects.requireNonNull(queryTodoProjectionRepository);
    }

    @Override
    public QueryTodoProjection execute(final GetTodoQueryRequest request) throws GetTodoBizQueryUseCaseException {
        return queryTodoProjectionRepository.findById(request.todoId());
    }
}
