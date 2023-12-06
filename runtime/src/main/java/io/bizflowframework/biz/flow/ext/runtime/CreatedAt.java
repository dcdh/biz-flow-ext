package io.bizflowframework.biz.flow.ext.runtime;

import java.time.LocalDateTime;
import java.util.Objects;

public record CreatedAt(LocalDateTime at) {
    public CreatedAt {
        Objects.requireNonNull(at);
    }
}
