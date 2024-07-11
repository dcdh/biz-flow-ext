package io.bizflowframework.biz.flow.ext.it.domain.query;

import io.bizflowframework.biz.flow.ext.it.domain.TodoId;
import io.bizflowframework.biz.flow.ext.runtime.usecase.ListOfProjection;
import io.bizflowframework.biz.flow.ext.runtime.usecase.Paging;

public interface QueryTodoProjectionRepository {

    void persist(QueryTodoProjection queryTodoProjection);

    QueryTodoProjection findById(TodoId todoId);

    ListOfProjection<QueryTodoProjection> findAll(Paging paging);
}
