package io.bizflowframework.biz.flow.ext.test.event;

import io.agroal.api.AgroalDataSource;
import io.bizflowframework.biz.flow.ext.runtime.creational.AggregateIdInstanceCreator;
import io.bizflowframework.biz.flow.ext.runtime.event.BaseJdbcPostgresqlEventRepository;
import io.bizflowframework.biz.flow.ext.runtime.serde.AggregateRootEventPayloadSerde;
import io.bizflowframework.biz.flow.ext.test.TodoAggregateRoot;
import io.bizflowframework.biz.flow.ext.test.TodoId;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Singleton;

// TODO generate it !!!
@Singleton
public final class TodoAggregateBaseJdbcPostgresqlEventRepositoryEnhanced extends BaseJdbcPostgresqlEventRepository<TodoId, TodoAggregateRoot> {
    public TodoAggregateBaseJdbcPostgresqlEventRepositoryEnhanced(final AgroalDataSource dataSource,
                                                                  final AggregateIdInstanceCreator aggregateIdInstanceCreator,
                                                                  final Instance<AggregateRootEventPayloadSerde<TodoAggregateRoot, ?>> aggregateRootEventPayloadsSerde) {
        super(dataSource, aggregateIdInstanceCreator, aggregateRootEventPayloadsSerde);
    }
}
