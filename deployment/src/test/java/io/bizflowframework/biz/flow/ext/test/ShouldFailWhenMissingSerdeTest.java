package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event.AggregateRootEventPayload;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenMissingSerdeTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(TodoId.class,
                            TodoAggregateRoot.class,
                            UnknownTodoEvent.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql")
            )
            .assertException(throwable -> assertThat(throwable)
                    .hasNoSuppressedExceptions()
                    .rootCause()
                    .hasMessage("Missing Serde for AggregateRoot 'io.bizflowframework.biz.flow.ext.test.TodoAggregateRoot' and AggregateEventPayload 'io.bizflowframework.biz.flow.ext.test.ShouldFailWhenMissingSerdeTest$UnknownTodoEvent'")
                    .hasNoSuppressedExceptions());

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    public record UnknownTodoEvent() implements AggregateRootEventPayload<TodoAggregateRoot> {
        @Override
        public void apply(final TodoAggregateRoot aggregateRoot) {
        }
    }
}
