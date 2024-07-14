package io.bizflowframework.biz.flow.ext.test.event;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.AggregateRootEventPayloadSerde;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.SerializedEventPayload;
import io.bizflowframework.biz.flow.ext.test.TodoAggregateRoot;
import jakarta.inject.Singleton;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@Singleton
public final class TodoCreatedAggregateRootEventPayloadSerdeEnhanced implements AggregateRootEventPayloadSerde<TodoAggregateRoot, TodoCreatedEvent> {
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