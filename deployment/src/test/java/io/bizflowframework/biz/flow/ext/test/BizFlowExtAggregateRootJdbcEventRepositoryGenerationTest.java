package io.bizflowframework.biz.flow.ext.test;

import io.agroal.api.AgroalDataSource;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRootRepository;
import io.bizflowframework.biz.flow.ext.test.event.*;
import io.quarkus.test.QuarkusUnitTest;
import jakarta.inject.Inject;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThatCode;

public class BizFlowExtAggregateRootJdbcEventRepositoryGenerationTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(TodoAggregateRoot.class)
                    .addClass(TodoId.class)
                    .addClass(TodoStatus.class)
                    .addClass(TodoCreatedEvent.class)
                    .addClass(TodoMarkedAsCompletedEvent.class)
                    .addClass(TodoCreatedEventSerde.class)
                    .addClass(TodoMarkedAsCompletedEventSerde.class)
                    .addClass(TodoAggregateRootRepository.class)
                    .addClass(StubbedDefaultCreatedAtProvider.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"));

    @Inject
    AggregateRootRepository<TodoId, TodoAggregateRoot> todoAggregateRootRepository;

    @Inject
    AgroalDataSource dataSource;

    @Test
    public void shouldLoadAggregateRootJdbcEventRepositoryGenerated() {
        // Given
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement insertFixturePreparedStatement = connection.prepareStatement(
                     "INSERT INTO T_EVENT (aggregaterootid, aggregateroottype, version, creationdate, eventtype, eventpayload) " +
                     "VALUES (?, ?, ?, ?, ?, to_json(?::json))")) {
            insertFixturePreparedStatement.setString(1, "shouldLoadAggregateRootJdbcEventRepositoryGenerated");
            insertFixturePreparedStatement.setString(2, "TodoAggregateRoot");
            insertFixturePreparedStatement.setLong(3, 0);
            insertFixturePreparedStatement.setObject(4, LocalDateTime.of(1983, Month.JULY, 27, 19, 30));
            insertFixturePreparedStatement.setString(5, "TodoCreatedEvent");
            insertFixturePreparedStatement.setString(6, "{\"description\": \"lorem ipsum dolor sit amet\"}");
            insertFixturePreparedStatement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        // When && Then
        assertThatCode(() -> todoAggregateRootRepository.load(new TodoId("shouldLoadAggregateRootJdbcEventRepositoryGenerated")))
                .doesNotThrowAnyException();
    }
}
