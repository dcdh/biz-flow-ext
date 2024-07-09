package io.bizflowframework.biz.flow.ext.runtime.api;

import io.bizflowframework.biz.flow.ext.runtime.usecase.Paging;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import java.util.Objects;

// https://github.com/jakartaee/rest/issues/913
public final class PagingDTO {
    @Parameter(schema = @Schema(type = SchemaType.INTEGER), required = true)
    @QueryParam("page[index]")
    Integer index;

    @Parameter(schema = @Schema(type = SchemaType.INTEGER), required = true)
    @QueryParam("page[size]")
    Integer size;

    public PagingDTO() {
    }

    public PagingDTO(final Integer index, final Integer size) {
        this.index = Objects.requireNonNull(index);
        this.size = Objects.requireNonNull(size);
    }

    public Paging toPaging() {
        Objects.requireNonNull(index);
        Objects.requireNonNull(size);
        return new Paging(index, size);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PagingDTO pagingDTO = (PagingDTO) o;
        return Objects.equals(index, pagingDTO.index) && Objects.equals(size, pagingDTO.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, size);
    }
}
