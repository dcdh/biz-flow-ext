package io.bizflowframework.biz.flow.ext.it;

import io.bizflowframework.biz.flow.ext.runtime.usecase.ListOfProjection;
import io.bizflowframework.biz.flow.ext.runtime.usecase.Paging;

public interface QueryTodoProjectionRepository {

    QueryTodoProjection findById(TodoId todoId);

    ListOfProjection<QueryTodoProjection> findAll(Paging paging);
}
