package io.bizflowframework.biz.flow.ext.test.event;

import io.agroal.api.AgroalDataSource;
import io.bizflowframework.biz.flow.ext.runtime.creational.AggregateIdInstanceCreator;
import io.bizflowframework.biz.flow.ext.runtime.event.JdbcPostgresqlEventRepository;
import io.bizflowframework.biz.flow.ext.runtime.serde.AggregateRootEventPayloadSerde;
import io.bizflowframework.biz.flow.ext.test.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.test.TodoId;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Singleton;

// TODO generate it !!!
@Singleton
public final class TodoAggregateJdbcPostgresqlEventRepository extends JdbcPostgresqlEventRepository<TodoId, TodoAggregateRoot> {
    public TodoAggregateJdbcPostgresqlEventRepository(final AgroalDataSource dataSource,
                                                      final AggregateIdInstanceCreator aggregateIdInstanceCreator,
                                                      final Instance<AggregateRootEventPayloadSerde<TodoAggregateRoot, ?>> aggregateRootEventPayloadsSerde) {
        super(dataSource, aggregateIdInstanceCreator, aggregateRootEventPayloadsSerde);
    }

    @Override
    protected Class<TodoId> aggregateIdClazz() {
        return TodoId.class;
    }
}
