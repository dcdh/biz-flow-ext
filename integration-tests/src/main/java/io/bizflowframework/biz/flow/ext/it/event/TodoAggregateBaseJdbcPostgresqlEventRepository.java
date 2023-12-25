package io.bizflowframework.biz.flow.ext.it.event;

import io.agroal.api.AgroalDataSource;
import io.bizflowframework.biz.flow.ext.it.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.it.TodoId;
import io.bizflowframework.biz.flow.ext.runtime.creational.AggregateIdInstanceCreator;
import io.bizflowframework.biz.flow.ext.runtime.event.BaseJdbcPostgresqlEventRepository;
import io.bizflowframework.biz.flow.ext.runtime.serde.AggregateRootEventPayloadSerde;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Singleton;

// TODO generate it !!!
@Singleton
public final class TodoAggregateBaseJdbcPostgresqlEventRepository extends BaseJdbcPostgresqlEventRepository<TodoId, TodoAggregateRoot> {
    public TodoAggregateBaseJdbcPostgresqlEventRepository(final AgroalDataSource dataSource,
                                                          final AggregateIdInstanceCreator aggregateIdInstanceCreator,
                                                          final Instance<AggregateRootEventPayloadSerde<TodoAggregateRoot, ?>> aggregateRootEventPayloadsSerde) {
        super(dataSource, aggregateIdInstanceCreator, aggregateRootEventPayloadsSerde);
    }
}
