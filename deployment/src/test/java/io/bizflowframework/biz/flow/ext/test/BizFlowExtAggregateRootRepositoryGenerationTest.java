package io.bizflowframework.biz.flow.ext.test;

import io.agroal.api.AgroalDataSource;
import io.bizflowframework.biz.flow.ext.runtime.AggregateRootRepository;
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

public class BizFlowExtAggregateRootRepositoryGenerationTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(TodoAggregateRoot.class)
                    .addClass(TodoId.class)
                    .addClass(TodoStatus.class)
                    .addClass(TodoCreated.class)
                    .addClass(TodoMarkedAsCompleted.class)
                    .addClass(TodoCreatedAggregateRootEventPayloadSerde.class)
                    .addClass(TodoMarkedAsCompletedAggregateRootEventPayloadSerde.class)
                    .addClass(UnknownTodoEvent.class)
                    .addClass(TodoAggregateBaseJdbcPostgresqlEventRepository.class)
                    .addClass(StubbedDefaultCreatedAtProvider.class)
                    .addClass(StubbedDefaultAggregateVersionIncrementer.class));

    @Inject
    AggregateRootRepository<TodoId, TodoAggregateRoot> todoAggregateRootRepository;

    @Inject
    AgroalDataSource dataSource;

    @Test
    public void shouldLoadAggregateRootGenerated() {
        // Given
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement insertFixturePreparedStatement = connection.prepareStatement(
                     "INSERT INTO T_EVENT (aggregaterootid, aggregateroottype, version, creationdate, eventtype, eventpayload) " +
                     "VALUES (?, ?, ?, ?, ?, to_json(?::json))")) {
            insertFixturePreparedStatement.setString(1, "shouldLoadAggregateRootGenerated");
            insertFixturePreparedStatement.setString(2, "TodoAggregateRoot");
            insertFixturePreparedStatement.setLong(3, 0);
            insertFixturePreparedStatement.setObject(4, LocalDateTime.of(1983, Month.JULY, 27, 19, 30));
            insertFixturePreparedStatement.setString(5, "TodoCreated");
            insertFixturePreparedStatement.setString(6, "{\"description\": \"lorem ipsum dolor sit amet\"}");
            insertFixturePreparedStatement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        // When && Then
        assertThatCode(() -> todoAggregateRootRepository.load(new TodoId("shouldLoadAggregateRootGenerated")))
                .doesNotThrowAnyException();
    }
}
