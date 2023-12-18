package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.AggregateId;
import io.bizflowframework.biz.flow.ext.runtime.AggregateRoot;
import io.bizflowframework.biz.flow.ext.runtime.CreatedAtProvider;
import io.bizflowframework.biz.flow.ext.runtime.event.AggregateRootEventPayload;
import io.bizflowframework.biz.flow.ext.runtime.incrementer.AggregateVersionIncrementer;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenEventNotARecordTest {
    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClass(TestId.class)
                    .addClass(TestAggregateRoot.class)
                    .addClass(InvalidEvent.class))
            .assertException(throwable -> assertThat(throwable)
                    .hasNoSuppressedExceptions()
                    .rootCause()
                    .hasMessage("Domain Event 'io.bizflowframework.biz.flow.ext.test.ShouldFailWhenEventNotARecordTest$InvalidEvent' must be a record")
                    .hasNoSuppressedExceptions());

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    record TestId(String id) implements AggregateId {
    }

    static final class TestAggregateRoot extends AggregateRoot<TestId, TestAggregateRoot> {
        public TestAggregateRoot(final TestId aggregateId,
                                 final CreatedAtProvider createdAtProvider,
                                 final AggregateVersionIncrementer aggregateVersionIncrementer) {
            super(aggregateId, createdAtProvider, aggregateVersionIncrementer);
        }
    }

    static final class InvalidEvent implements AggregateRootEventPayload<TestAggregateRoot> {
        @Override
        public void apply(final TestAggregateRoot aggregateRoot) {
        }
    }
}
