package io.bizflowframework.biz.flow.ext.it.event;

import io.bizflowframework.biz.flow.ext.it.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.runtime.serde.AggregateRootEventPayloadSerde;
import io.bizflowframework.biz.flow.ext.runtime.serde.SerializedEventPayload;
import jakarta.inject.Singleton;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@Singleton
public final class TodoCreatedAggregateRootEventPayloadSerde implements AggregateRootEventPayloadSerde<TodoId, TodoAggregateRoot, TodoCreated> {
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

    @Override
    public Class<TodoAggregateRoot> aggregateRootClass() {
        return TodoAggregateRoot.class;
    }

    @Override
    public Class<TodoCreated> aggregateRootEventPayloadClass() {
        return TodoCreated.class;
    }
}
