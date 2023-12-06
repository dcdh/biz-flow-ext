package io.bizflowframework.biz.flow.ext.runtime;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class AggregateVersionTest {

    @Test
    public void shouldVersionBeDefinedToMinusOneByDefault() {
        assertThat(new AggregateVersion().version())
                .isEqualTo(-1);
    }

    @Test
    public void shouldBeUninitializedByDefault() {
        assertThat(new AggregateVersion().isUninitialized())
                .isEqualTo(true);
    }

    @Test
    public void shouldIncrementVersion() {
        assertAll(
                () -> assertThat(new AggregateVersion().increment()).isEqualTo(new AggregateVersion(0)),
                () -> assertThat(new AggregateVersion().increment().version()).isEqualTo(0));
    }

    @Test
    public void shouldBeInitializedWhenVersionIsIncremented() {
        assertThat(new AggregateVersion().increment().isUninitialized())
                .isEqualTo(false);
    }

    @Test
    public void shouldVerifyEquality() {
        EqualsVerifier.forClass(AggregateVersion.class).verify();
    }
}