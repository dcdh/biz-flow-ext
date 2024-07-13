package io.bizflowframework.biz.flow.ext.it.domain.usecase;

import io.bizflowframework.biz.flow.ext.it.domain.query.QueryTodoProjection;
import io.bizflowframework.biz.flow.ext.it.domain.query.QueryTodoProjectionRepository;
import io.bizflowframework.biz.flow.ext.runtime.usecase.BizQueryUseCase;
import io.bizflowframework.biz.flow.ext.runtime.usecase.ListOfProjection;

import java.util.Objects;

public final class ListTodosBizQueryUseCase implements BizQueryUseCase<ListOfProjection<QueryTodoProjection>, ListTodosQueryRequest, ListTodosBizQueryUseCaseException> {

    private final QueryTodoProjectionRepository queryTodoProjectionRepository;

    public ListTodosBizQueryUseCase(final QueryTodoProjectionRepository queryTodoProjectionRepository) {
        this.queryTodoProjectionRepository = Objects.requireNonNull(queryTodoProjectionRepository);
    }

    @Override
    public ListOfProjection<QueryTodoProjection> execute(final ListTodosQueryRequest request) throws ListTodosBizQueryUseCaseException {
        return queryTodoProjectionRepository.findAll(request.paging());
    }
}
