package io.bizflowframework.biz.flow.ext.it.domain.usecase;

import io.bizflowframework.biz.flow.ext.it.domain.query.QueryTodoProjection;
import io.bizflowframework.biz.flow.ext.it.domain.query.QueryTodoProjectionRepository;
import io.bizflowframework.biz.flow.ext.runtime.usecase.BizQueryUseCase;
import io.bizflowframework.biz.flow.ext.runtime.usecase.ListOfProjection;

import java.util.Objects;

public final class ListTodosUseCase implements BizQueryUseCase<ListOfProjection<QueryTodoProjection>, ListTodosRequest, ListTodosUseCaseException> {

    private final QueryTodoProjectionRepository queryTodoProjectionRepository;

    public ListTodosUseCase(final QueryTodoProjectionRepository queryTodoProjectionRepository) {
        this.queryTodoProjectionRepository = Objects.requireNonNull(queryTodoProjectionRepository);
    }

    @Override
    public ListOfProjection<QueryTodoProjection> execute(final ListTodosRequest request) throws ListTodosUseCaseException {
        return queryTodoProjectionRepository.findAll(request.paging());
    }
}