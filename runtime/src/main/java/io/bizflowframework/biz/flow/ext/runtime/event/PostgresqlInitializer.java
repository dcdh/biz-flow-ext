package io.bizflowframework.biz.flow.ext.runtime.event;

import dev.failsafe.RetryPolicy;
import io.agroal.api.AgroalDataSource;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.Objects;

@Singleton
public final class PostgresqlInitializer {
    private static final String POSTGRESQL_DDL_FILE = "/sql/event-store-postgresql.ddl";
    private static final Logger log = Logger.getLogger(PostgresqlInitializer.class);

    private final AgroalDataSource dataSource;

    public PostgresqlInitializer(final AgroalDataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
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
        try (final Connection con = dataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            final String ddl = new String(ddlResource.readAllBytes(), StandardCharsets.UTF_8);
            stmt.executeUpdate(ddl);
        } catch (final IOException | SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}
