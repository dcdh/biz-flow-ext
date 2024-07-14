package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.AggregateRootEventPayloadSerde;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.SerializedEventPayload;
import io.bizflowframework.biz.flow.ext.test.event.TodoCreatedEvent;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenMultipleSerdeOfSameEventAreImplementedTest {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(TodoId.class,
                            TodoAggregateRoot.class,
                            TodoCreatedEvent.class,
                            TodoCreatedAggregateRootEventPayloadSerdeOne.class,
                            TodoCreatedAggregateRootEventPayloadSerdeTwo.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql")
            )
            .assertException(throwable -> assertThat(throwable)
                    .hasMessageContaining("Multiple Serde implementations found for aggregate 'io.bizflowframework.biz.flow.ext.test.TodoAggregateRoot' and event 'io.bizflowframework.biz.flow.ext.test.event.TodoCreatedEvent', only one is expected. Found implementations io.bizflowframework.biz.flow.ext.test.ShouldFailWhenMultipleSerdeOfSameEventAreImplementedTest$TodoCreatedAggregateRootEventPayloadSerdeOne, io.bizflowframework.biz.flow.ext.test.ShouldFailWhenMultipleSerdeOfSameEventAreImplementedTest$TodoCreatedAggregateRootEventPayloadSerdeTwo"));

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    private static final class TodoCreatedAggregateRootEventPayloadSerdeOne implements AggregateRootEventPayloadSerde<TodoAggregateRoot, TodoCreatedEvent> {
        @Override
        public SerializedEventPayload serialize(final TodoCreatedEvent selfAggregateRootEventPayload) {
            throw new IllegalStateException("Should not be called");
        }

        @Override
        public TodoCreatedEvent deserialize(final SerializedEventPayload serializedEventPayload) {
            throw new IllegalStateException("Should not be called");
        }

        @Override
        public Class<TodoAggregateRoot> aggregateRootClass() {
            return TodoAggregateRoot.class;
        }

        @Override
        public Class<TodoCreatedEvent> aggregateRootEventPayloadClass() {
            return TodoCreatedEvent.class;
        }
    }

    private static final class TodoCreatedAggregateRootEventPayloadSerdeTwo implements AggregateRootEventPayloadSerde<TodoAggregateRoot, TodoCreatedEvent> {
        @Override
        public SerializedEventPayload serialize(final TodoCreatedEvent selfAggregateRootEventPayload) {
            throw new IllegalStateException("Should not be called");
        }

        @Override
        public TodoCreatedEvent deserialize(final SerializedEventPayload serializedEventPayload) {
            throw new IllegalStateException("Should not be called");
        }

        @Override
        public Class<TodoAggregateRoot> aggregateRootClass() {
            return TodoAggregateRoot.class;
        }

        @Override
        public Class<TodoCreatedEvent> aggregateRootEventPayloadClass() {
            return TodoCreatedEvent.class;
        }
    }
}
