package io.bizflowframework.biz.flow.ext.runtime.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.agroal.pool.DataSource;
import io.bizflowframework.biz.flow.ext.runtime.event.PostgresInitializedEvent;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.context.ManagedExecutor;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class InitializeDebeziumConnector {

    private static final Pattern JDBC_URL_PATTERN = Pattern.compile(
            "jdbc:postgresql://([\\w.-]+)(?::(\\d+))?/(\\w+)");

    private final ManagedExecutor executor;
    private final ObjectMapper objectMapper;
    private final String username;
    private final String password;
    private final String hostname;
    private final String port;
    private final String database;

    public InitializeDebeziumConnector(final ManagedExecutor executor,
                                       final ObjectMapper objectMapper,
                                       @ConfigProperty(name = "quarkus.datasource.username") final String username,
                                       @ConfigProperty(name = "quarkus.datasource.password") final String password,
                                       @ConfigProperty(name = "quarkus.datasource.jdbc.url") final String jdbcUrl) {
        this.executor = Objects.requireNonNull(executor);
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);
        Objects.requireNonNull(jdbcUrl);
        final Matcher matcher = JDBC_URL_PATTERN.matcher(jdbcUrl);
        if (matcher.matches()) {
            this.hostname = matcher.group(1);
            this.port = matcher.group(2);
            this.database = matcher.group(3);
        } else {
            throw new IllegalStateException("JDBC URL is not a valid JDBC URL: " + jdbcUrl);
        }
    }

    void onPostgresInitialized(@Observes PostgresInitializedEvent event) {
        final Properties props = new Properties();
        props.setProperty("name", "engine");
        props.setProperty("connector.class", "io.debezium.connector.postgresql.PostgresConnector");
        props.setProperty("converter.schemas.enable", "false"); // don't include schema in message
        props.setProperty("database.hostname", hostname);
        props.setProperty("database.port", port);
        props.setProperty("database.user", username);
        props.setProperty("database.password", password);
        props.setProperty("database.dbname", database);
        props.setProperty("table.include.list", "public.T_EVENT");
        il me faut le "bon" transformer ...

        try (final DebeziumEngine<ChangeEvent<String, String>> engine = DebeziumEngine.create(Json.class)
                .using(props)
//                .notifying(record -> Log.info(record))
                .notifying(((records, committer) -> {
                    for (final ChangeEvent<String, String> record : records) {
//                        ici je dois faire le ncessaire pour decoder du json
//                                je dois trouver le bon deserializer et faire un truc avec
//                                ou un handler bien specifique plutot ...
                        committer.markProcessed(record);
                    }
                }))
                .build()
        ) {
            // Run the engine asynchronously ...
            executor.execute(engine);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
