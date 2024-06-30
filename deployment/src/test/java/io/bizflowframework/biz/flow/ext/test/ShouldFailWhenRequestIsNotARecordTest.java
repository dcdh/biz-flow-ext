package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.usecase.Request;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenRequestIsNotARecordTest {
    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClass(TestAggregateId.class)
                    .addClass(InvalidRequest.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"))
            .assertException(throwable -> assertThat(throwable)
                    .hasNoSuppressedExceptions()
                    .rootCause()
                    .hasMessage("Request 'io.bizflowframework.biz.flow.ext.test.ShouldFailWhenRequestIsNotARecordTest$InvalidRequest' must be a record")
                    .hasNoSuppressedExceptions());

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    record TestAggregateId(String id) implements AggregateId {
    }

    static final class InvalidRequest implements Request<TestAggregateId> {
        @Override
        public TestAggregateId aggregateId() {
            return null;
        }
    }
}
