package io.bizflowframework.biz.flow.ext.it.infra.event;

import io.bizflowframework.biz.flow.ext.it.domain.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.domain.event.TodoCreated;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.AggregateRootEventPayloadSerde;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.SerializedEventPayload;
import jakarta.inject.Singleton;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@Singleton
public final class TodoCreatedAggregateRootEventPayloadSerde implements AggregateRootEventPayloadSerde<TodoAggregateRoot, TodoCreated> {
    @Override
    public SerializedEventPayload serialize(final TodoCreated selfAggregateRootEventPayload) {
        final String event = Json.createObjectBuilder()
                .add(TodoAggregateRoot.DESCRIPTION, selfAggregateRootEventPayload.description())
                .build()
                .toString();
        return new SerializedEventPayload(event);
    }

    @Override
    public TodoCreated deserialize(final SerializedEventPayload serializedEventPayload) {
        final JsonObject jsonObject = Json.createReader(serializedEventPayload.reader()).readObject();
        return new TodoCreated(
                jsonObject.getString(TodoAggregateRoot.DESCRIPTION));
    }
}
