package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.command.AggregateCommandRequest;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenAggregateCommandRequestIsNotARecordTest {
    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClass(TestAggregateId.class)
                    .addClass(InvalidCommandRequest.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"))
            .assertException(throwable -> assertThat(throwable)
                    .hasNoSuppressedExceptions()
                    .rootCause()
                    .hasMessage("AggregateCommandRequest 'io.bizflowframework.biz.flow.ext.test.ShouldFailWhenAggregateCommandRequestIsNotARecordTest$InvalidCommandRequest' must be a record")
                    .hasNoSuppressedExceptions());

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    private record TestAggregateId(String id) implements AggregateId {
    }

    private static final class InvalidCommandRequest implements AggregateCommandRequest<TestAggregateId> {
        @Override
        public TestAggregateId aggregateId() {
            throw new RuntimeException("Should not be called !");
        }
    }
}
