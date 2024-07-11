package io.bizflowframework.biz.flow.ext.it.domain.query;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class QueryTodoProjectionTest {
    @Test
    public void shouldVerifyEquality() {
        EqualsVerifier
                .forClass(QueryTodoProjection.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}