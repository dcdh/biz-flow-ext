package io.bizflowframework.biz.flow.ext.test.usecase;

import io.bizflowframework.biz.flow.ext.runtime.usecase.BizQueryUseCase;
import io.bizflowframework.biz.flow.ext.runtime.usecase.ListOfProjection;
import io.bizflowframework.biz.flow.ext.runtime.usecase.NbOfElements;
import io.bizflowframework.biz.flow.ext.test.query.QueryTodoProjection;

import java.util.List;

public final class ListTodoBizQueryUseCase implements BizQueryUseCase<ListOfProjection<QueryTodoProjection>, ListTodoQueryRequest, ListTodoBizQueryUseCaseException> {
    @Override
    public ListOfProjection<QueryTodoProjection> execute(ListTodoQueryRequest request) throws ListTodoBizQueryUseCaseException {
        return new ListOfProjection<>(List.of(), new NbOfElements(0L));
    }
}
