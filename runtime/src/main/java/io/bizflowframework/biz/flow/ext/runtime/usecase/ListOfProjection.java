package io.bizflowframework.biz.flow.ext.runtime.usecase;

import java.util.List;
import java.util.Objects;

public record ListOfProjection<T extends VersionedProjection>(List<T> projections,
                                                              NbOfElements nbOfElements) implements Projection {
    public ListOfProjection {
        Objects.requireNonNull(projections);
        Objects.requireNonNull(nbOfElements);
    }
}
