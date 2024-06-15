package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.command.Command;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenCommandIsNotARecordTest {
    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClass(TestAggregateId.class)
                    .addClass(InvalidCommand.class)
                    .addAsResource("application.properties"))
            .assertException(throwable -> assertThat(throwable)
                    .hasNoSuppressedExceptions()
                    .rootCause()
                    .hasMessage("Command 'io.bizflowframework.biz.flow.ext.test.ShouldFailWhenCommandIsNotARecordTest$InvalidCommand' must be a record")
                    .hasNoSuppressedExceptions());

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    record TestAggregateId(String id) implements AggregateId {
    }

    static final class InvalidCommand implements Command<TestAggregateId> {
        @Override
        public TestAggregateId aggregateId() {
            return null;
        }
    }
}
