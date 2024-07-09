package io.bizflowframework.biz.flow.ext.it.api.query;

import io.bizflowframework.biz.flow.ext.it.QueryTodoProjection;
import io.bizflowframework.biz.flow.ext.it.QueryTodoProjectionRepository;
import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.runtime.usecase.ListOfProjection;
import io.bizflowframework.biz.flow.ext.runtime.usecase.NbOfElements;
import io.bizflowframework.biz.flow.ext.runtime.usecase.Paging;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public final class PanacheQueryTodoProjectionRepository implements QueryTodoProjectionRepository {

    @Override
    public QueryTodoProjection findById(final TodoId todoId) {
        return QueryTodo.findById(todoId.id());
    }

    @Override
    public ListOfProjection<QueryTodoProjection> findAll(final Paging paging) {
        final PanacheQuery<QueryTodo> query = QueryTodo.findAll(Sort.by("createdAt", Sort.Direction.Ascending));
        final Long count = query.count();
        final List<QueryTodoProjection> projections = query
                .page(Page.of(paging.index(), paging.size()))
                .stream().map(QueryTodoProjection.class::cast)
                .toList();
        return new ListOfProjection<>(projections, new NbOfElements(count));
    }
}
