package io.bizflowframework.biz.flow.ext.runtime.api;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class PagingDTOTest {

    @Test
    void shouldVerifyEquality() {
        EqualsVerifier.simple().forClass(PagingDTO.class).verify();
    }
}