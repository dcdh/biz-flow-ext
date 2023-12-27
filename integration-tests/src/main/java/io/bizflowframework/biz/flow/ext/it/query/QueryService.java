package io.bizflowframework.biz.flow.ext.it.query;

import io.bizflowframework.biz.flow.ext.it.TodoId;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

@Singleton
public final class QueryService {

    private final Map<TodoId, QueryEntity> queryEntities = new HashMap<>();

    public void store(final QueryEntity queryEntity) {
        this.queryEntities.put(queryEntity.todoId(), queryEntity);
    }

    public QueryEntity getByTodoId(final TodoId todoId) {
        return queryEntities.get(todoId);
    }
}

