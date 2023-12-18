package io.bizflowframework.biz.flow.ext.it.event;

import io.bizflowframework.biz.flow.ext.it.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.runtime.serde.AggregateRootEventPayloadSerde;
import io.bizflowframework.biz.flow.ext.runtime.serde.SerializedEventPayload;
import jakarta.inject.Singleton;
import jakarta.json.Json;

@Singleton
public final class TodoMarkedAsCompletedAggregateRootEventPayloadSerde implements AggregateRootEventPayloadSerde<TodoAggregateRoot, TodoMarkedAsCompleted> {
    @Override
    public SerializedEventPayload serialize(final TodoMarkedAsCompleted selfAggregateRootEventPayload) {
        final String event = Json.createObjectBuilder()
                .build()
                .toString();
        return new SerializedEventPayload(event);
    }

    @Override
    public TodoMarkedAsCompleted deserialize(final SerializedEventPayload serializedEventPayload) {
        return new TodoMarkedAsCompleted();
    }

    @Override
    public Class<TodoAggregateRoot> aggregateRootClass() {
        return TodoAggregateRoot.class;
    }

    @Override
    public Class<TodoMarkedAsCompleted> aggregateRootEventPayloadClass() {
        return TodoMarkedAsCompleted.class;
    }
}
