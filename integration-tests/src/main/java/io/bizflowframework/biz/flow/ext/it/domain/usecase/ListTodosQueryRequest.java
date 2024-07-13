package io.bizflowframework.biz.flow.ext.it.domain.usecase;

import io.bizflowframework.biz.flow.ext.runtime.usecase.ListOfQueryRequest;
import io.bizflowframework.biz.flow.ext.runtime.usecase.Paging;

import java.util.Objects;

public record ListTodosQueryRequest(Paging paging) implements ListOfQueryRequest {
    public ListTodosQueryRequest {
        Objects.requireNonNull(paging);
    }
}
