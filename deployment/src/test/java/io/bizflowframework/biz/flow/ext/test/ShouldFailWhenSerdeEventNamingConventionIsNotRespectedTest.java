package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.AggregateRootEventPayloadSerde;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.SerializedEventPayload;
import io.bizflowframework.biz.flow.ext.test.event.TodoCreatedEvent;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenSerdeEventNamingConventionIsNotRespectedTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(TodoId.class, TodoAggregateRoot.class, TodoCreatedEventSerdeBadNaming.class, TodoCreatedEvent.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql")
            )
            .assertException(throwable -> assertThat(throwable)
                    .hasNoSuppressedExceptions()
                    .rootCause()
                    .hasMessage("Bad naming for 'io.bizflowframework.biz.flow.ext.test.ShouldFailWhenSerdeEventNamingConventionIsNotRespectedTest$TodoCreatedEventSerdeBadNaming', expected naming 'TodoCreatedEventSerde'")
                    .hasNoSuppressedExceptions());

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    private static final class TodoCreatedEventSerdeBadNaming implements AggregateRootEventPayloadSerde<TodoAggregateRoot, TodoCreatedEvent> {

        @Override
        public SerializedEventPayload serialize(final TodoCreatedEvent selfAggregateRootEventPayload) {
            throw new IllegalStateException("Should not be called");
        }

        @Override
        public TodoCreatedEvent deserialize(final SerializedEventPayload serializedEventPayload) {
            throw new IllegalStateException("Should not be called");
        }
    }
}
