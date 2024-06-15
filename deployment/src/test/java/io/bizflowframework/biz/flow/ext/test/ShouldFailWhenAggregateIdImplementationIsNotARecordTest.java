package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenAggregateIdImplementationIsNotARecordTest {
    @RegisterExtension
    static final QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClass(InvalidAggregateId.class)
                    .addAsResource("application.properties"))
            .assertException(throwable -> assertThat(throwable)
                    .hasNoSuppressedExceptions()
                    .rootCause()
                    .hasMessage("AggregateId 'io.bizflowframework.biz.flow.ext.test.ShouldFailWhenAggregateIdImplementationIsNotARecordTest$InvalidAggregateId' must be a record")
                    .hasNoSuppressedExceptions());

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    static final class InvalidAggregateId implements AggregateId {

        @Override
        public String id() {
            return null;
        }
    }
}
