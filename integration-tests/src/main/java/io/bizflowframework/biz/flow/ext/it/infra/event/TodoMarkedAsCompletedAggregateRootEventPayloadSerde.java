package io.bizflowframework.biz.flow.ext.it.infra.event;

import io.bizflowframework.biz.flow.ext.it.domain.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.domain.event.TodoMarkedAsCompletedEvent;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.AggregateRootEventPayloadSerde;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.serde.SerializedEventPayload;
import jakarta.inject.Singleton;
import jakarta.json.Json;

@Singleton
public final class TodoMarkedAsCompletedAggregateRootEventPayloadSerde implements AggregateRootEventPayloadSerde<TodoAggregateRoot, TodoMarkedAsCompletedEvent> {
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
}
