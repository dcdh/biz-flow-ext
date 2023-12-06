package io.bizflowframework.biz.flow.ext.runtime;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public record CreatedAt(LocalDateTime at) implements Serializable {
    public CreatedAt {
        Objects.requireNonNull(at);
    }
}
