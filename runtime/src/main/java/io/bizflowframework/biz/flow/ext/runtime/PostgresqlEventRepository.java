package io.bizflowframework.biz.flow.ext.runtime;

import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import io.agroal.api.AgroalDataSource;

import java.io.InputStream;
import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

@Singleton
public final class PostgresqlEventRepository implements EventRepository {
    private static final String POSTGRESQL_DDL_FILE = "/sql/event-store-postgresql.ddl";

    private final AgroalDataSource dataSource;
    private final AggregateRootEventPayloadSerde aggregateRootEventPayloadSerde;humm je devrais peu être injecter une Instance puis faire un filtre !!!!

    public PostgresqlEventRepository(final AgroalDataSource dataSource,
                                     final AggregateRootEventPayloadSerde aggregateRootEventPayloadSerde) {
        this.dataSource = Objects.requireNonNull(dataSource);
        this.aggregateRootEventPayloadSerde = Objects.requireNonNull(aggregateRootEventPayloadSerde);
    }

    public void onStart(@Observes final StartupEvent event) {
        // Use a retry mechanism in case of multiple instances running in //
        // TODO Should use Flyway if it is present in the application using this extension.
        // DDL will be started each time an instance of the application is started
        // DDL must use an IF NOT EXIST syntax
        final RetryPolicy<Object> retryPolicy = RetryPolicy.builder()
                .handle(Exception.class)
                .withDelay(Duration.ofMillis(100))
                .withMaxRetries(100)
                .build();
        final InputStream ddlResource = Objects.requireNonNull(this.getClass().getResourceAsStream(POSTGRESQL_DDL_FILE));
        try (final Scanner scanner = new Scanner(ddlResource).useDelimiter("!!");
             final Connection con = dataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            while (scanner.hasNext()) {
                final String ddlEntry = scanner.next().trim();
                if (!ddlEntry.isEmpty()) {
                    Failsafe.with(retryPolicy).run(() -> stmt.executeUpdate(ddlEntry));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public <T extends AggregateRoot<T>> void save(final AggregateRootDomainEvent<T> aggregateRootDomainEvent) {
        Objects.requireNonNull(aggregateRootDomainEvent);
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement preparedStatement = eventToSave.insertStatement(connection)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public <T extends AggregateRoot<T>> List<AggregateRootDomainEvent<T>> loadOrderByVersionASC(final AggregateId aggregateId) {
        Objects.requireNonNull(aggregateId);
        try (final Connection connection = mutableDataSource.getConnection();
             final PreparedStatement stmt = connection.prepareStatement("SELECT * FROM EVENT e WHERE e.aggregaterootid = ? AND e.aggregateroottype = ? ORDER BY e.version ASC")) {
            stmt.setString(1, aggregateRootId.aggregateRootId());
            stmt.setString(2, aggregateRootId.aggregateRootType());
            final List<AggregateRootEvent> aggregateRootEvents = new ArrayList<>();
            // TODO I should get the number of event to initialize list size. However, a lot of copies will be made in memory on large result set.
            try (final ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    aggregateRootEvents.add(new PostgreSQLDecryptableEvent(resultSet).toEvent(aggregateRootEventPayloadsDeSerializer));
                }
            }
            return aggregateRootEvents;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public <T extends AggregateRoot<T>> List<AggregateRootDomainEvent<T>> loadOrderByVersionASC(final AggregateId aggregateId,
                                                                                                final AggregateVersion aggregateVersion) {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(aggregateVersion);
        try (final Connection connection = mutableDataSource.getConnection();
             final PreparedStatement stmt = connection.prepareStatement("SELECT * FROM EVENT e WHERE e.aggregaterootid = ? AND e.aggregateroottype = ? AND e.version <= ? ORDER BY e.version ASC")) {
            stmt.setString(1, aggregateRootId.aggregateRootId());
            stmt.setString(2, aggregateRootId.aggregateRootType());
            stmt.setLong(3, version);
            final List<AggregateRootEvent> aggregateRootEvents = new ArrayList<>(version.intValue());
            try (final ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    aggregateRootEvents.add(new PostgreSQLDecryptableEvent(resultSet).toEvent(aggregateRootEventPayloadsDeSerializer));
                }
            }
            return aggregateRootEvents;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
