package io.bizflowframework.biz.flow.ext.it.infra.postgres;

import io.bizflowframework.biz.flow.ext.it.domain.TodoId;
import io.bizflowframework.biz.flow.ext.it.domain.query.QueryTodoProjection;
import io.bizflowframework.biz.flow.ext.it.domain.query.QueryTodoProjectionRepository;
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
    public void persist(final QueryTodoProjection queryTodoProjection) {
        QueryTodo.getEntityManager().merge(new QueryTodo(queryTodoProjection));
    }

    @Override
    public QueryTodoProjection findById(final TodoId todoId) {
        return QueryTodo.<QueryTodo>findById(todoId.id()).toQueryTodoProjection();
    }

    @Override
    public ListOfProjection<QueryTodoProjection> findAll(final Paging paging) {
        final PanacheQuery<QueryTodo> query = QueryTodo.findAll(Sort.by("createdAt", Sort.Direction.Ascending));
        final Long count = query.count();
        final List<QueryTodoProjection> projections = query
                .page(Page.of(paging.index(), paging.size()))
                .stream().map(QueryTodo::toQueryTodoProjection)
                .toList();
        return new ListOfProjection<>(projections, new NbOfElements(count));
    }
}
