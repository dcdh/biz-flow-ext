package io.bizflowframework.biz.flow.ext.runtime.usecase;

import java.util.Objects;

public record NbOfElements(Long nb) {
    public NbOfElements {
        Objects.requireNonNull(nb);
    }
}
