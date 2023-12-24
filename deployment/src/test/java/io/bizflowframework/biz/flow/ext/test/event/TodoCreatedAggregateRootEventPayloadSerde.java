package io.bizflowframework.biz.flow.ext.test.event;

import io.bizflowframework.biz.flow.ext.runtime.serde.AggregateRootEventPayloadSerde;
import io.bizflowframework.biz.flow.ext.runtime.serde.SerializedEventPayload;
import io.bizflowframework.biz.flow.ext.test.TodoAggregateRoot;
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

    /**
     * // access flags 0x1
     * // signature ()Ljava/lang/Class<Lio/bizflowframework/biz/flow/ext/test/TodoAggregateRoot;>;
     * // declaration: java.lang.Class<io.bizflowframework.biz.flow.ext.test.TodoAggregateRoot> aggregateRootClass()
     * public aggregateRootClass()Ljava/lang/Class;
     * L0
     * LINENUMBER 31 L0
     * LDC Lio/bizflowframework/biz/flow/ext/test/TodoAggregateRoot;.class
     * ARETURN
     * L1
     * LOCALVARIABLE this Lio/bizflowframework/biz/flow/ext/test/event/TodoCreatedAggregateRootEventPayloadSerde; L0 L1 0
     * MAXSTACK = 1
     * MAXLOCALS = 1
     */
    @Override
    public Class<TodoAggregateRoot> aggregateRootClass() {
        return TodoAggregateRoot.class;
    }

    /**
     * // access flags 0x1
     * // signature ()Ljava/lang/Class<Lio/bizflowframework/biz/flow/ext/test/event/TodoCreated;>;
     * // declaration: java.lang.Class<io.bizflowframework.biz.flow.ext.test.event.TodoCreated> aggregateRootEventPayloadClass()
     * public aggregateRootEventPayloadClass()Ljava/lang/Class;
     * L0
     * LINENUMBER 36 L0
     * LDC Lio/bizflowframework/biz/flow/ext/test/event/TodoCreated;.class
     * ARETURN
     * L1
     * LOCALVARIABLE this Lio/bizflowframework/biz/flow/ext/test/event/TodoCreatedAggregateRootEventPayloadSerde; L0 L1 0
     * MAXSTACK = 1
     * MAXLOCALS = 1
     */
    @Override
    public Class<TodoCreated> aggregateRootEventPayloadClass() {
        return TodoCreated.class;
    }
}
