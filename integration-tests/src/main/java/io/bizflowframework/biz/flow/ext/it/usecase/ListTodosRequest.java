package io.bizflowframework.biz.flow.ext.it.usecase;

import io.bizflowframework.biz.flow.ext.runtime.usecase.ListOfQueryRequest;
import io.bizflowframework.biz.flow.ext.runtime.usecase.Paging;

import java.util.Objects;

public record ListTodosRequest(Paging paging) implements ListOfQueryRequest {
    public ListTodosRequest {
        Objects.requireNonNull(paging);
    }
}
