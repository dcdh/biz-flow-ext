package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.AggregateRootEventPayloadSerde;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.SerializedEventPayload;
import io.bizflowframework.biz.flow.ext.test.event.*;
import io.quarkus.test.QuarkusUnitTest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class BizFlowExtSerdeCodeEnhancementTest {
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                            .addClass(TodoAggregateRoot.class)
                            .addClass(TodoId.class)
                            .addClass(TodoStatus.class)
                            .addClass(TodoCreatedEvent.class)
                            .addClass(TodoCreatedEventSerde.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"));

    @Inject
    TodoCreatedEventSerde todoCreatedEventSerdeEnhanced;

    @Test
    public void shouldReturnAggregateRootClass() {
        assertThat(todoCreatedEventSerdeEnhanced.aggregateRootClass())
                .isEqualTo(TodoAggregateRoot.class);
    }

    @Test
    public void shouldReturnAggregateRootEventPayloadClass() {
        assertThat(todoCreatedEventSerdeEnhanced.aggregateRootEventPayloadClass())
                .isEqualTo(TodoCreatedEvent.class);
    }

    // before TodoCreatedEventSerdeEnhanced : name no more valid
    @Singleton
    public static final class TodoCreatedEventSerde implements AggregateRootEventPayloadSerde<TodoAggregateRoot, TodoCreatedEvent> {
        @Override
        public SerializedEventPayload serialize(final TodoCreatedEvent selfAggregateRootEventPayload) {
            final String event = Json.createObjectBuilder()
                    .add(TodoAggregateRoot.DESCRIPTION, selfAggregateRootEventPayload.description())
                    .build()
                    .toString();
            return new SerializedEventPayload(event);
        }

        @Override
        public TodoCreatedEvent deserialize(final SerializedEventPayload serializedEventPayload) {
            final JsonObject jsonObject = Json.createReader(serializedEventPayload.reader()).readObject();
            return new TodoCreatedEvent(
                    jsonObject.getString(TodoAggregateRoot.DESCRIPTION));
        }
    }
}
