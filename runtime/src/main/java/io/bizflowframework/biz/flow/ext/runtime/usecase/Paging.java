package io.bizflowframework.biz.flow.ext.runtime.usecase;

import java.util.Objects;

public record Paging(Integer index, Integer size) {
    public Paging {
        Objects.requireNonNull(index);
        Objects.requireNonNull(size);
    }
}
