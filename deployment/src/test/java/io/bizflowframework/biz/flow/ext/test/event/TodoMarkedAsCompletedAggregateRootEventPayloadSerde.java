package io.bizflowframework.biz.flow.ext.test.event;

import io.bizflowframework.biz.flow.ext.runtime.serde.AggregateRootEventPayloadSerde;
import io.bizflowframework.biz.flow.ext.runtime.serde.SerializedEventPayload;
import io.bizflowframework.biz.flow.ext.test.TodoAggregateRoot;
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

    /**
     * // access flags 0x1
     * // signature ()Ljava/lang/Class<Lio/bizflowframework/biz/flow/ext/test/TodoAggregateRoot;>;
     * // declaration: java.lang.Class<io.bizflowframework.biz.flow.ext.test.TodoAggregateRoot> aggregateRootClass()
     * public aggregateRootClass()Ljava/lang/Class;
     * L0
     * LINENUMBER 26 L0
     * LDC Lio/bizflowframework/biz/flow/ext/test/TodoAggregateRoot;.class
     * ARETURN
     * L1
     * LOCALVARIABLE this Lio/bizflowframework/biz/flow/ext/test/event/TodoMarkedAsCompletedAggregateRootEventPayloadSerde; L0 L1 0
     * MAXSTACK = 1
     * MAXLOCALS = 1
     */
    @Override
    public Class<TodoAggregateRoot> aggregateRootClass() {
        return TodoAggregateRoot.class;
    }

    /**
     * // access flags 0x1
     * // signature ()Ljava/lang/Class<Lio/bizflowframework/biz/flow/ext/test/event/TodoMarkedAsCompleted;>;
     * // declaration: java.lang.Class<io.bizflowframework.biz.flow.ext.test.event.TodoMarkedAsCompleted> aggregateRootEventPayloadClass()
     * public aggregateRootEventPayloadClass()Ljava/lang/Class;
     * L0
     * LINENUMBER 31 L0
     * LDC Lio/bizflowframework/biz/flow/ext/test/event/TodoMarkedAsCompleted;.class
     * ARETURN
     * L1
     * LOCALVARIABLE this Lio/bizflowframework/biz/flow/ext/test/event/TodoMarkedAsCompletedAggregateRootEventPayloadSerde; L0 L1 0
     * MAXSTACK = 1
     * MAXLOCALS = 1
     */
    @Override
    public Class<TodoMarkedAsCompleted> aggregateRootEventPayloadClass() {
        return TodoMarkedAsCompleted.class;
    }
}
