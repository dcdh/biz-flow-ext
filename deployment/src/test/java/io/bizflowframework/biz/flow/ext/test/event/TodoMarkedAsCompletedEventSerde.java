package io.bizflowframework.biz.flow.ext.test.event;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.AggregateRootEventPayloadSerde;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.SerializedEventPayload;
import io.bizflowframework.biz.flow.ext.test.TodoAggregateRoot;
import jakarta.inject.Singleton;
import jakarta.json.Json;

@Singleton
public final class TodoMarkedAsCompletedEventSerde implements AggregateRootEventPayloadSerde<TodoAggregateRoot, TodoMarkedAsCompletedEvent> {
    @Override
    public SerializedEventPayload serialize(final TodoMarkedAsCompletedEvent selfAggregateRootEventPayload) {
        final String event = Json.createObjectBuilder()
                .build()
                .toString();
        return new SerializedEventPayload(event);
    }

    @Override
    public TodoMarkedAsCompletedEvent deserialize(final SerializedEventPayload serializedEventPayload) {
        return new TodoMarkedAsCompletedEvent();
    }

    @Override
    public Class<TodoAggregateRoot> aggregateRootClass() {
        return TodoAggregateRoot.class;
    }

    @Override
    public Class<TodoMarkedAsCompletedEvent> aggregateRootEventPayloadClass() {
        return TodoMarkedAsCompletedEvent.class;
    }
}
