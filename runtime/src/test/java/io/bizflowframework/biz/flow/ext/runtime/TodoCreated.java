package io.bizflowframework.biz.flow.ext.runtime;

public class TodoCreated implements AggregateRootEventPayload<TodoAggregate> {
    @Override
    public void apply(final TodoAggregate aggregateRoot) {
        aggregateRoot.on(this);
    }
}
FCK dans mes tests je vais devoir charger une base de données postgres !!!
FCK tester que la serialization / deserizalisation fnctionnent !!!