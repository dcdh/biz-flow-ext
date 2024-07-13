package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event.AggregateRootEventPayload;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.AggregateRootEventPayloadSerde;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.SerializedEventPayload;
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
                            TodoCreated.class,
                            TodoCreatedAggregateRootEventPayloadSerdeOne.class,
                            TodoCreatedAggregateRootEventPayloadSerdeTwo.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql")
            )
            .assertException(throwable -> assertThat(throwable)
                    .hasNoSuppressedExceptions()
                    .rootCause()
                    .hasMessage("Multiple implementations found for Serde 'AggregateRootEventPayloadSerdeKey[aggregateRootClassName=io.bizflowframework.biz.flow.ext.test.TodoAggregateRoot, eventPayloadClassName=io.bizflowframework.biz.flow.ext.test.ShouldFailWhenMultipleSerdeOfSameEventAreImplementedTest$TodoCreated]', only one is expected. Found implementations io.bizflowframework.biz.flow.ext.test.ShouldFailWhenMultipleSerdeOfSameEventAreImplementedTest$TodoCreatedAggregateRootEventPayloadSerdeOne, io.bizflowframework.biz.flow.ext.test.ShouldFailWhenMultipleSerdeOfSameEventAreImplementedTest$TodoCreatedAggregateRootEventPayloadSerdeTwo")
                    .hasNoSuppressedExceptions());

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    private record TodoCreated() implements AggregateRootEventPayload<TodoAggregateRoot> {
        @Override
        public void apply(TodoAggregateRoot aggregateRoot) {

        }
    }

    private final class TodoCreatedAggregateRootEventPayloadSerdeOne implements AggregateRootEventPayloadSerde<TodoAggregateRoot, TodoCreated> {
        @Override
        public SerializedEventPayload serialize(final TodoCreated selfAggregateRootEventPayload) {
            throw new IllegalStateException("Should not be called");
        }

        @Override
        public TodoCreated deserialize(final SerializedEventPayload serializedEventPayload) {
            throw new IllegalStateException("Should not be called");
        }

        @Override
        public Class<TodoAggregateRoot> aggregateRootClass() {
            return TodoAggregateRoot.class;
        }

        @Override
        public Class<TodoCreated> aggregateRootEventPayloadClass() {
            return TodoCreated.class;
        }
    }

    private final class TodoCreatedAggregateRootEventPayloadSerdeTwo implements AggregateRootEventPayloadSerde<TodoAggregateRoot, TodoCreated> {
        @Override
        public SerializedEventPayload serialize(final TodoCreated selfAggregateRootEventPayload) {
            throw new IllegalStateException("Should not be called");
        }

        @Override
        public TodoCreated deserialize(final SerializedEventPayload serializedEventPayload) {
            throw new IllegalStateException("Should not be called");
        }

        @Override
        public Class<TodoAggregateRoot> aggregateRootClass() {
            return TodoAggregateRoot.class;
        }

        @Override
        public Class<TodoCreated> aggregateRootEventPayloadClass() {
            return TodoCreated.class;
        }
    }
}
