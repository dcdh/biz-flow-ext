package io.bizflowframework.biz.flow.ext.test;

import io.agroal.api.AgroalDataSource;
import io.bizflowframework.biz.flow.ext.runtime.*;
import io.bizflowframework.biz.flow.ext.runtime.event.EventType;
import io.bizflowframework.biz.flow.ext.runtime.serde.MissingSerdeException;
import io.bizflowframework.biz.flow.ext.test.event.*;
import io.quarkus.test.QuarkusUnitTest;
import jakarta.inject.Inject;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class BizFlowExtAggregateRootRepositoryTest {

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
                    .addClass(TodoAggregateRootRepository.class)
                    .addClass(StubbedDefaultCreatedAtProvider.class)
                    .addClass(StubbedDefaultAggregateVersionIncrementer.class));

    @Inject
    AggregateRootRepository<TodoId, TodoAggregateRoot> todoAggregateRootRepository;

    @Inject
    AgroalDataSource dataSource;

    @Inject
    StubbedDefaultCreatedAtProvider stubbedDefaultCreatedAtProvider;

    @Inject
    StubbedDefaultAggregateVersionIncrementer stubbedDefaultAggregateVersionIncrementer;

    @AfterEach
    public void tearDown() {
        // Generally the database table are flushed after each test. Likes this, each test is guarantee to run without being polluted by other tests states.
        // However, it will not be done here because TRUNCATE and DELETE are prevented by a trigger to avoid event store alteration.
        // This is an expected production behavior, and it must not be altered by the tests (tests must reflect as possible the reality).
        // So each test must use a unique identifier.
    }

    @Test
    public void shouldStoreEvent() {
        // Given
        stubbedDefaultCreatedAtProvider.addResponse(new CreatedAt(LocalDateTime.of(1983, Month.JULY, 27, 19, 30)));
        final TodoAggregateRoot givenTodoAggregateRoot = new TodoAggregateRoot(new TodoId("shouldStoreEvent"),
                stubbedDefaultCreatedAtProvider, stubbedDefaultAggregateVersionIncrementer);
        givenTodoAggregateRoot.createNewTodo("lorem ipsum dolor sit amet");

        // When
        final TodoAggregateRoot saved = todoAggregateRootRepository.save(givenTodoAggregateRoot);

        // Then
        assertAll(
                () -> assertThat(saved.aggregateRootIdentifier()).isEqualTo(new AggregateRootIdentifier<>(
                        new TodoId("shouldStoreEvent"),
                        new AggregateType("TodoAggregateRoot")
                )),
                () -> assertThat(saved.aggregateId()).isEqualTo(new TodoId("shouldStoreEvent")),
                () -> assertThat(saved.aggregateVersion()).isEqualTo(new AggregateVersion(0)),
                () -> assertThat(saved.description()).isEqualTo("lorem ipsum dolor sit amet"),
                () -> assertThat(saved.status()).isEqualTo(TodoStatus.IN_PROGRESS));

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement countPreparedStatement = connection.prepareStatement(
                     "SELECT COUNT(*) AS count FROM T_EVENT WHERE aggregaterootid = 'shouldStoreEvent'");
             final PreparedStatement selectEventPreparedStatement = connection.prepareStatement(
                     "SELECT aggregaterootid, aggregateroottype, version, creationdate, eventtype, eventpayload FROM T_EVENT WHERE aggregaterootid = 'shouldStoreEvent'")) {
            final ResultSet countResultSet = countPreparedStatement.executeQuery();
            countResultSet.next();
            assertThat(countResultSet.getInt("count")).isEqualTo(1);

            final ResultSet eventResultSet = selectEventPreparedStatement.executeQuery();
            eventResultSet.next();
            assertAll(
                    () -> assertThat(eventResultSet.getString("aggregaterootid")).isEqualTo("shouldStoreEvent"),
                    () -> assertThat(eventResultSet.getString("aggregateroottype")).isEqualTo("TodoAggregateRoot"),
                    () -> assertThat(eventResultSet.getInt("version")).isEqualTo(0),
                    () -> assertThat(eventResultSet.getObject("creationdate", LocalDateTime.class))
                            .isEqualTo(LocalDateTime.of(1983, Month.JULY, 27, 19, 30)),
                    () -> assertThat(eventResultSet.getString("eventtype")).isEqualTo("TodoCreated"),
                    () -> assertThat(eventResultSet.getString("eventpayload"))
                            .isEqualTo("{\"description\": \"lorem ipsum dolor sit amet\"}")
            );
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldFailToStoreWhenSerdeIsMissing() {
        // Given
        final TodoAggregateRoot givenTodoAggregateRoot = new TodoAggregateRoot(new TodoId("shouldFailToStoreWhenSerdeIsMissing"),
                stubbedDefaultCreatedAtProvider, stubbedDefaultAggregateVersionIncrementer);
        givenTodoAggregateRoot.createNewTodo("lorem ipsum dolor sit amet");
        givenTodoAggregateRoot.addUnknownTodoEvent();

        // When & Then
        assertThatThrownBy(() -> todoAggregateRootRepository.save(givenTodoAggregateRoot))
                .isInstanceOf(MissingSerdeException.class)
                .hasFieldOrPropertyWithValue("aggregateType", new AggregateType("TodoAggregateRoot"))
                .hasFieldOrPropertyWithValue("eventType", new EventType("UnknownTodoEvent"));
    }

    @Test
    public void shouldFailToStoreAnEventHavingSameIdTypeAndVersion() {
        // Given
        stubbedDefaultAggregateVersionIncrementer
                .addResponse(new AggregateVersion(0))
                .addResponse(new AggregateVersion(0));
        final TodoAggregateRoot givenTodoAggregateRoot = new TodoAggregateRoot(new TodoId("shouldFailToStoreAnEventHavingSameIdTypeAndVersion"),
                stubbedDefaultCreatedAtProvider, stubbedDefaultAggregateVersionIncrementer);
        givenTodoAggregateRoot.createNewTodo("lorem ipsum dolor sit amet");
        givenTodoAggregateRoot.markTodoAsCompleted();

        // When & Then
        assertThatThrownBy(() -> todoAggregateRootRepository.save(givenTodoAggregateRoot))
                .isInstanceOf(EventStoreException.class)
                .hasCauseInstanceOf(SQLException.class)
                .hasMessageContaining("ERROR: Event already present while should not be")
                .satisfies(throwable -> assertThat(((EventStoreException) throwable).isForbidden()).isTrue());
    }

    @Test
    public void shouldFailToStoreAnEventWhenVersionPlusOneIsNotRespected() {
        // Given
        stubbedDefaultAggregateVersionIncrementer
                .addResponse(new AggregateVersion(0))
                .addResponse(new AggregateVersion(2));
        final TodoAggregateRoot givenTodoAggregateRoot = new TodoAggregateRoot(new TodoId("shouldFailToStoreAnEventWhenVersionPlusOneIsNotRespected"),
                stubbedDefaultCreatedAtProvider, stubbedDefaultAggregateVersionIncrementer);
        givenTodoAggregateRoot.createNewTodo("lorem ipsum dolor sit amet");
        givenTodoAggregateRoot.markTodoAsCompleted();

        // When & Then
        assertThatThrownBy(() -> todoAggregateRootRepository.save(givenTodoAggregateRoot))
                .isInstanceOf(EventStoreException.class)
                .hasCauseInstanceOf(SQLException.class)
                .hasMessageContaining("ERROR: Previous event version mismatch - current version 2 - previous version 0")
                .satisfies(throwable -> assertThat(((EventStoreException) throwable).isForbidden()).isTrue());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "DELETE FROM T_EVENT WHERE aggregaterootid = 'shouldFailToDeleteEvent'",
            "TRUNCATE TABLE T_EVENT"
    })
    public void shouldFailToDeleteEvent(final String deleteQuery) {
        // Given
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement insertFixturePreparedStatement = connection.prepareStatement(
                     "INSERT INTO T_EVENT (aggregaterootid, aggregateroottype, version, creationdate, eventtype, eventpayload) " +
                     "SELECT ?, ?, ?, ?, ?, to_json(?::json) WHERE NOT EXISTS (SELECT NULL FROM T_EVENT WHERE aggregaterootid = ?)"
             )) {
            insertFixturePreparedStatement.setString(1, "shouldFailToDeleteEvent");
            insertFixturePreparedStatement.setString(2, "TodoAggregateRoot");
            insertFixturePreparedStatement.setLong(3, 0);
            insertFixturePreparedStatement.setObject(4, LocalDateTime.of(1983, Month.JULY, 27, 19, 30));
            insertFixturePreparedStatement.setString(5, "TodoCreated");
            insertFixturePreparedStatement.setString(6, "{\"description\": \"lorem ipsum dolor sit amet\"}");
            insertFixturePreparedStatement.setString(7, "shouldFailToDeleteEvent");
            insertFixturePreparedStatement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        // When & Then
        assertThatThrownBy(() -> {
            try (final Connection connection = dataSource.getConnection();
                 final PreparedStatement deletePreparedStatement = connection.prepareStatement(deleteQuery)) {
                deletePreparedStatement.execute();
            }
        })
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("ERROR: not allowed");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "UPDATE T_EVENT SET aggregaterootid = 'shouldFailToUpdateAnEventBOOM' WHERE aggregaterootid = 'shouldFailToUpdateAnEvent'",
            "UPDATE T_EVENT SET aggregateroottype = 'TodoAggregateRootBOOM' WHERE aggregaterootid = 'shouldFailToUpdateAnEvent'",
            "UPDATE T_EVENT SET version = 1 WHERE aggregaterootid = 'shouldFailToUpdateAnEvent'",
            "UPDATE T_EVENT SET creationdate = NOW() WHERE aggregaterootid = 'shouldFailToUpdateAnEvent'",
            "UPDATE T_EVENT SET eventtype = 'TodoCreatedBOOM' WHERE aggregaterootid = 'shouldFailToUpdateAnEvent'",
            "UPDATE T_EVENT SET eventpayload = '{\"description\": \"BOOM\"}' WHERE aggregaterootid = 'shouldFailToUpdateAnEvent'"
    })
    public void shouldFailToUpdateAnEvent(final String updateQuery) {
        // Given
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement insertFixturePreparedStatement = connection.prepareStatement(
                     "INSERT INTO T_EVENT (aggregaterootid, aggregateroottype, version, creationdate, eventtype, eventpayload) " +
                     "SELECT ?, ?, ?, ?, ?, to_json(?::json) WHERE NOT EXISTS (SELECT NULL FROM T_EVENT WHERE aggregaterootid = ?)"
             )) {
            insertFixturePreparedStatement.setString(1, "shouldFailToUpdateAnEvent");
            insertFixturePreparedStatement.setString(2, "TodoAggregateRoot");
            insertFixturePreparedStatement.setLong(3, 0);
            insertFixturePreparedStatement.setObject(4, LocalDateTime.of(1983, Month.JULY, 27, 19, 30));
            insertFixturePreparedStatement.setString(5, "TodoCreated");
            insertFixturePreparedStatement.setString(6, "{\"description\": \"lorem ipsum dolor sit amet\"}");
            insertFixturePreparedStatement.setString(7, "shouldFailToUpdateAnEvent");
            insertFixturePreparedStatement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        // When & Then
        assertThatThrownBy(() -> {
            try (final Connection connection = dataSource.getConnection();
                 final PreparedStatement deletePreparedStatement = connection.prepareStatement(updateQuery)) {
                deletePreparedStatement.execute();
            }
        })
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("ERROR: not allowed");
    }

    @Test
    public void shouldLoadAggregateRoot() {
        // Given
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement insertFixturePreparedStatement = connection.prepareStatement(
                     "INSERT INTO T_EVENT (aggregaterootid, aggregateroottype, version, creationdate, eventtype, eventpayload) " +
                     "VALUES (?, ?, ?, ?, ?, to_json(?::json))")) {
            insertFixturePreparedStatement.setString(1, "shouldLoadAggregateRoot");
            insertFixturePreparedStatement.setString(2, "TodoAggregateRoot");
            insertFixturePreparedStatement.setLong(3, 0);
            insertFixturePreparedStatement.setObject(4, LocalDateTime.of(1983, Month.JULY, 27, 19, 30));
            insertFixturePreparedStatement.setString(5, "TodoCreated");
            insertFixturePreparedStatement.setString(6, "{\"description\": \"lorem ipsum dolor sit amet\"}");
            insertFixturePreparedStatement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        // When
        final TodoAggregateRoot loaded = todoAggregateRootRepository.load(new TodoId("shouldLoadAggregateRoot"));

        // Then
        assertAll(
                () -> assertThat(loaded.aggregateRootIdentifier()).isEqualTo(new AggregateRootIdentifier<>(
                        new TodoId("shouldLoadAggregateRoot"),
                        new AggregateType("TodoAggregateRoot")
                )),
                () -> assertThat(loaded.aggregateId()).isEqualTo(new TodoId("shouldLoadAggregateRoot")),
                () -> assertThat(loaded.aggregateVersion()).isEqualTo(new AggregateVersion(0)),
                () -> assertThat(loaded.description()).isEqualTo("lorem ipsum dolor sit amet"),
                () -> assertThat(loaded.status()).isEqualTo(TodoStatus.IN_PROGRESS));
    }

    @Test
    public void shouldLoadAggregateRootByVersion() {
        // Given
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement insertFixturePreparedStatement = connection.prepareStatement(
                     "INSERT INTO T_EVENT (aggregaterootid, aggregateroottype, version, creationdate, eventtype, eventpayload) " +
                     "VALUES (?, ?, ?, ?, ?, to_json(?::json))")) {
            insertFixturePreparedStatement.setString(1, "shouldLoadAggregateRootByVersion");
            insertFixturePreparedStatement.setString(2, "TodoAggregateRoot");
            insertFixturePreparedStatement.setLong(3, 0);
            insertFixturePreparedStatement.setObject(4, LocalDateTime.of(1983, Month.JULY, 27, 19, 30));
            insertFixturePreparedStatement.setString(5, "TodoCreated");
            insertFixturePreparedStatement.setString(6, "{\"description\": \"lorem ipsum dolor sit amet\"}");
            insertFixturePreparedStatement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        // When
        final TodoAggregateRoot loaded = todoAggregateRootRepository.load(new TodoId("shouldLoadAggregateRootByVersion"), new AggregateVersion(0));

        // Then
        assertAll(
                () -> assertThat(loaded.aggregateRootIdentifier()).isEqualTo(new AggregateRootIdentifier<>(
                        new TodoId("shouldLoadAggregateRootByVersion"),
                        new AggregateType("TodoAggregateRoot")
                )),
                () -> assertThat(loaded.aggregateId()).isEqualTo(new TodoId("shouldLoadAggregateRootByVersion")),
                () -> assertThat(loaded.aggregateVersion()).isEqualTo(new AggregateVersion(0)),
                () -> assertThat(loaded.description()).isEqualTo("lorem ipsum dolor sit amet"),
                () -> assertThat(loaded.status()).isEqualTo(TodoStatus.IN_PROGRESS));
    }

    @Test
    public void shouldFailWhenAggregateIsNotPresent() {
        assertThatThrownBy(() -> todoAggregateRootRepository.load(new TodoId("shouldFailWhenAggregateIsNotPresent"), new AggregateVersion(1)))
                .isInstanceOf(UnknownAggregateRootException.class)
                .hasFieldOrPropertyWithValue("aggregateRootIdentifier", new AggregateRootIdentifier<>(
                        new TodoId("shouldFailWhenAggregateIsNotPresent"), new AggregateType("TodoAggregateRoot")));
    }

    @Test
    public void shouldFailWhenVersionIsNotPresent() {
        // Given
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement insertFixturePreparedStatement = connection.prepareStatement(
                     "INSERT INTO T_EVENT (aggregaterootid, aggregateroottype, version, creationdate, eventtype, eventpayload) " +
                     "VALUES (?, ?, ?, ?, ?, to_json(?::json))")) {
            insertFixturePreparedStatement.setString(1, "shouldFailWhenVersionIsNotPresent");
            insertFixturePreparedStatement.setString(2, "TodoAggregateRoot");
            insertFixturePreparedStatement.setLong(3, 0);
            insertFixturePreparedStatement.setObject(4, LocalDateTime.of(1983, Month.JULY, 27, 19, 30));
            insertFixturePreparedStatement.setString(5, "TodoCreated");
            insertFixturePreparedStatement.setString(6, "{\"description\": \"lorem ipsum dolor sit amet\"}");
            insertFixturePreparedStatement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
        // When & Then
        assertThatThrownBy(() -> todoAggregateRootRepository.load(new TodoId("shouldFailWhenVersionIsNotPresent"), new AggregateVersion(1)))
                .isInstanceOf(UnknownAggregateRootAtVersionException.class)
                .hasFieldOrPropertyWithValue("aggregateRootIdentifier", new AggregateRootIdentifier<>(
                        new TodoId("shouldFailWhenVersionIsNotPresent"), new AggregateType("TodoAggregateRoot")))
                .hasFieldOrPropertyWithValue("aggregateVersion", new AggregateVersion(1));
    }

    @Test
    public void shouldFailToLoadWhenSerdeIsMissing() {
        // Given
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement insertFixturePreparedStatement = connection.prepareStatement(
                     "INSERT INTO T_EVENT (aggregaterootid, aggregateroottype, version, creationdate, eventtype, eventpayload) " +
                     "VALUES (?, ?, ?, ?, ?, to_json(?::json))")) {
            insertFixturePreparedStatement.setString(1, "shouldFailToLoadWhenSerdeIsMissing");
            insertFixturePreparedStatement.setString(2, "TodoAggregateRoot");
            insertFixturePreparedStatement.setLong(3, 0);
            insertFixturePreparedStatement.setObject(4, LocalDateTime.of(1983, Month.JULY, 27, 19, 30));
            insertFixturePreparedStatement.setString(5, "UnknownTodoEvent");
            insertFixturePreparedStatement.setString(6, "{}");
            insertFixturePreparedStatement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        // When & Then
        assertThatThrownBy(() -> todoAggregateRootRepository.load(new TodoId("shouldFailToLoadWhenSerdeIsMissing")))
                .isInstanceOf(MissingSerdeException.class)
                .hasFieldOrPropertyWithValue("aggregateType", new AggregateType("TodoAggregateRoot"))
                .hasFieldOrPropertyWithValue("eventType", new EventType("UnknownTodoEvent"));
    }

    @Test
    public void shouldFailToLoadByVersionWhenSerdeIsMissing() {
        // Given
        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement insertFixturePreparedStatement = connection.prepareStatement(
                     "INSERT INTO T_EVENT (aggregaterootid, aggregateroottype, version, creationdate, eventtype, eventpayload) " +
                     "VALUES (?, ?, ?, ?, ?, to_json(?::json))")) {
            insertFixturePreparedStatement.setString(1, "shouldFailToLoadByVersionWhenSerdeIsMissing");
            insertFixturePreparedStatement.setString(2, "TodoAggregateRoot");
            insertFixturePreparedStatement.setLong(3, 0);
            insertFixturePreparedStatement.setObject(4, LocalDateTime.of(1983, Month.JULY, 27, 19, 30));
            insertFixturePreparedStatement.setString(5, "UnknownTodoEvent");
            insertFixturePreparedStatement.setString(6, "{}");
            insertFixturePreparedStatement.executeUpdate();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        // When & Then
        assertThatThrownBy(() -> todoAggregateRootRepository.load(new TodoId("shouldFailToLoadByVersionWhenSerdeIsMissing"), new AggregateVersion(0)))
                .isInstanceOf(MissingSerdeException.class)
                .hasFieldOrPropertyWithValue("aggregateType", new AggregateType("TodoAggregateRoot"))
                .hasFieldOrPropertyWithValue("eventType", new EventType("UnknownTodoEvent"));
    }
}
