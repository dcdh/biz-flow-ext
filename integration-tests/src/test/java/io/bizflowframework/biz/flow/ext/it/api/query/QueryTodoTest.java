package io.bizflowframework.biz.flow.ext.it.api.query;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

public class QueryTodoTest {

    @Test
    void should_verify_equality_on_id_only() {
        EqualsVerifier.forClass(QueryTodo.class)
                .suppress(Warning.SURROGATE_KEY)
                .verify();
    }
}
