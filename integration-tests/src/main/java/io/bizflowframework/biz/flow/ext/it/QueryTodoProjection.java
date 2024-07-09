package io.bizflowframework.biz.flow.ext.it;

import io.bizflowframework.biz.flow.ext.runtime.usecase.VersionedProjection;

public interface QueryTodoProjection extends VersionedProjection {
    String description();

    TodoStatus status();
}
