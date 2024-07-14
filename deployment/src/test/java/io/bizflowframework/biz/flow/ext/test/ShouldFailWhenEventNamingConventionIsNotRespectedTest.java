package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event.AggregateRootEventPayload;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.AggregateRootEventPayloadSerde;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.SerializedEventPayload;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenEventNamingConventionIsNotRespectedTest {
    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(TodoId.class, TodoAggregateRoot.class, InvalidTodoEventBadNaming.class, InvalidTodoEventBadNamingSerde.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql")
            )
            .assertException(throwable -> assertThat(throwable)
                    .hasNoSuppressedExceptions()
                    .rootCause()
                    .hasMessage("Bad naming for 'io.bizflowframework.biz.flow.ext.test.ShouldFailWhenEventNamingConventionIsNotRespectedTest$InvalidTodoEventBadNaming', must end with 'Event'")
                    .hasNoSuppressedExceptions());

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    private static record InvalidTodoEventBadNaming() implements AggregateRootEventPayload<TodoAggregateRoot> {
        @Override
        public void apply(TodoAggregateRoot aggregateRoot) {

        }
    }

    private static final class InvalidTodoEventBadNamingSerde implements AggregateRootEventPayloadSerde<TodoAggregateRoot, InvalidTodoEventBadNaming> {

        @Override
        public SerializedEventPayload serialize(final InvalidTodoEventBadNaming selfAggregateRootEventPayload) {
            throw new IllegalStateException("Should not be called");
        }

        @Override
        public InvalidTodoEventBadNaming deserialize(final SerializedEventPayload serializedEventPayload) {
            throw new IllegalStateException("Should not be called");
        }
    }

}
