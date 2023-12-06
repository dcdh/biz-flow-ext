package io.bizflowframework.biz.flow.ext.runtime.event;

import io.agroal.api.AgroalDataSource;
import io.bizflowframework.biz.flow.ext.runtime.*;
import io.bizflowframework.biz.flow.ext.runtime.creational.AggregateIdInstanceCreator;
import io.bizflowframework.biz.flow.ext.runtime.serde.AggregateRootEventPayloadSerde;
import io.bizflowframework.biz.flow.ext.runtime.serde.MissingSerdeException;
import io.bizflowframework.biz.flow.ext.runtime.serde.SerializedEventPayload;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Singleton
public abstract class JdbcPostgresqlEventRepository<ID extends AggregateId, T extends AggregateRoot<ID, T>> implements EventRepository<ID, T> {
    private final AgroalDataSource dataSource;
    private final AggregateIdInstanceCreator aggregateIdInstanceCreator;
    private final Instance<AggregateRootEventPayloadSerde<ID, T, ?>> aggregateRootEventPayloadsSerde;

    public JdbcPostgresqlEventRepository(final AgroalDataSource dataSource,
                                         final AggregateIdInstanceCreator aggregateIdInstanceCreator,
                                         final Instance<AggregateRootEventPayloadSerde<ID, T, ?>> aggregateRootEventPayloadsSerde) {
        this.dataSource = Objects.requireNonNull(dataSource);
        this.aggregateIdInstanceCreator = Objects.requireNonNull(aggregateIdInstanceCreator);
        this.aggregateRootEventPayloadsSerde = Objects.requireNonNull(aggregateRootEventPayloadsSerde);
    }

    @Override
    @Transactional
    public void save(final AggregateRootDomainEvent<ID, T> aggregateRootDomainEvent) throws MissingSerdeException, EventStoreException {
        Objects.requireNonNull(aggregateRootDomainEvent);
        final AggregateRootEventPayloadSerde aggregateRootEventPayloadSerde = getSerdeInstance(
                aggregateRootDomainEvent.aggregateType(), aggregateRootDomainEvent.eventType());
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO T_EVENT (aggregaterootid, aggregateroottype, version, creationdate, eventtype, eventpayload) " +
                     "VALUES (?, ?, ?, ?, ?, to_json(?::json))")) {
            final AggregateRootIdentifier<ID> aggregateRootIdentifier = aggregateRootDomainEvent.aggregateRootIdentifier();
            final SerializedEventPayload serializedEventPayload = aggregateRootEventPayloadSerde.serialize(aggregateRootDomainEvent.payload());
            preparedStatement.setString(1, aggregateRootIdentifier.aggregateId().id());
            preparedStatement.setString(2, aggregateRootIdentifier.aggregateType().type());
            preparedStatement.setLong(3, aggregateRootDomainEvent.aggregateVersion().version());
            preparedStatement.setObject(4, aggregateRootDomainEvent.createdAt().at());
            preparedStatement.setString(5, aggregateRootDomainEvent.eventType().type());
            preparedStatement.setString(6, serializedEventPayload.payload());
            preparedStatement.executeUpdate();
        } catch (final SQLException e) {
            throw new EventStoreException(e);
        }
    }

    @Override
    @Transactional
    public List<AggregateRootDomainEvent> loadOrderByVersionASC(final AggregateRootIdentifier<ID> aggregateRootIdentifier)
            throws MissingSerdeException, EventStoreException {
        Objects.requireNonNull(aggregateRootIdentifier);
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement stmt = connection.prepareStatement(
                     "SELECT * FROM T_EVENT e WHERE e.aggregaterootid = ? AND e.aggregateroottype = ? ORDER BY e.version ASC")) {
            stmt.setString(1, aggregateRootIdentifier.aggregateId().id());
            stmt.setString(2, aggregateRootIdentifier.aggregateType().type());
            final List<AggregateRootDomainEvent> aggregateRootDomainEvents = new ArrayList<>();
            // TODO I should get the number of event to initialize list size. However, a lot of copies will be made in memory on large result set.
            try (final ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    aggregateRootDomainEvents.add(toAggregateRootDomainEvent(resultSet));
                }
            }
            return aggregateRootDomainEvents;
        } catch (final SQLException e) {
            throw new EventStoreException(e);
        }
    }

    @Override
    @Transactional
    public List<AggregateRootDomainEvent> loadOrderByVersionASC(final AggregateRootIdentifier<ID> aggregateRootIdentifier,
                                                                final AggregateVersion aggregateVersion)
            throws MissingSerdeException, EventStoreException {
        Objects.requireNonNull(aggregateRootIdentifier);
        Objects.requireNonNull(aggregateVersion);
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement stmt = connection.prepareStatement(
                     "SELECT * FROM T_EVENT e WHERE e.aggregaterootid = ? AND e.aggregateroottype = ? AND e.version <= ? ORDER BY e.version ASC")) {
            stmt.setString(1, aggregateRootIdentifier.aggregateId().id());
            stmt.setString(2, aggregateRootIdentifier.aggregateType().type());
            stmt.setLong(3, aggregateVersion.version());
            final List<AggregateRootDomainEvent> aggregateRootDomainEvents = new ArrayList<>(aggregateVersion.version());
            try (final ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    aggregateRootDomainEvents.add(toAggregateRootDomainEvent(resultSet));
                }
            }
            return aggregateRootDomainEvents;
        } catch (final SQLException e) {
            throw new EventStoreException(e);
        }
    }

    private AggregateRootDomainEvent<ID, T> toAggregateRootDomainEvent(final ResultSet resultSet) throws SQLException, MissingSerdeException {
        final AggregateType aggregateType = new AggregateType(resultSet.getString("aggregateroottype"));
        final EventType eventType = new EventType(resultSet.getString("eventtype"));
        final AggregateRootEventPayloadSerde<ID, T, ?> aggregateRootEventPayloadSerde = getSerdeInstance(aggregateType, eventType);
        final ID aggregateId = aggregateIdInstanceCreator.createInstance(aggregateIdClazz(), resultSet.getString("aggregaterootid"));
        return new AggregateRootDomainEvent<>(
                new AggregateRootIdentifier<>(
                        aggregateId,
                        aggregateType),
                new AggregateVersion(resultSet.getInt("version")),
                new CreatedAt(resultSet.getObject("creationdate", LocalDateTime.class)),
                aggregateRootEventPayloadSerde.deserialize(new SerializedEventPayload(resultSet.getString("eventpayload"))));
    }

    private AggregateRootEventPayloadSerde<ID, T, ?> getSerdeInstance(
            final AggregateType aggregateType, final EventType eventType) throws MissingSerdeException {
        return aggregateRootEventPayloadsSerde.stream()
                .filter(instance -> aggregateType.equals(new AggregateType(instance.aggregateRootClass())))
                .filter(instance -> eventType.equals(new EventType(instance.aggregateRootEventPayloadClass())))
                .findFirst()
                .orElseThrow(() -> new MissingSerdeException(aggregateType, eventType));
    }

    protected abstract Class<ID> aggregateIdClazz();
}
