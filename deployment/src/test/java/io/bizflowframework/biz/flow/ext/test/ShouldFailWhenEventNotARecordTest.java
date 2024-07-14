package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.event.AggregateRootEventPayload;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.AggregateRootEventPayloadSerde;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.SerializedEventPayload;
import io.quarkus.test.QuarkusUnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class ShouldFailWhenEventNotARecordTest {
    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot(jar -> jar
                    .addClasses(TodoId.class, TodoAggregateRoot.class, InvalidTodoEvent.class, InvalidTodoEventPayloadSerde.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql")
            )
            // CLASS bean [types=[io.bizflowframework.biz.flow.ext.runtime.event.EventRepository<io.bizflowframework.biz.flow.ext.test.ShouldFailWhenEventNotARecordTest$TestId, io.bizflowframework.biz.flow.ext.test.ShouldFailWhenEventNotARecordTest$TestAggregateRoot>, io.bizflowframework.biz.flow.ext.test.ShouldFailWhenEventNotARecordTest$TestAggregateRootJdbcPostgresqlEventRepositoryGenerated, java.lang.Object, io.bizflowframework.biz.flow.ext.runtime.event.BaseJdbcPostgresqlEventRepository<io.bizflowframework.biz.flow.ext.test.ShouldFailWhenEventNotARecordTest$TestId, io.bizflowframework.biz.flow.ext.test.ShouldFailWhenEventNotARecordTest$TestAggregateRoot>], qualifiers=[@Default, @Any], target=io.bizflowframework.biz.flow.ext.test.ShouldFailWhenEventNotARecordTest$TestAggregateRootJdbcPostgresqlEventRepositoryGenerated]
            .assertException(throwable -> assertThat(throwable)
                    .hasNoSuppressedExceptions()
                    .rootCause()
                    .hasMessage("Domain Event 'io.bizflowframework.biz.flow.ext.test.ShouldFailWhenEventNotARecordTest$InvalidTodoEvent' must be a record")
                    .hasNoSuppressedExceptions());

    @Test
    public void test() {
        Assertions.fail("Startup should have failed");
    }

    private static final class InvalidTodoEvent implements AggregateRootEventPayload<TodoAggregateRoot> {
        @Override
        public void apply(TodoAggregateRoot aggregateRoot) {

        }
    }

    private static final class InvalidTodoEventPayloadSerde implements AggregateRootEventPayloadSerde<TodoAggregateRoot, InvalidTodoEvent> {

        @Override
        public SerializedEventPayload serialize(final InvalidTodoEvent selfAggregateRootEventPayload) {
            throw new IllegalStateException("Should not be called");
        }

        @Override
        public InvalidTodoEvent deserialize(final SerializedEventPayload serializedEventPayload) {
            throw new IllegalStateException("Should not be called");
        }
    }

}
