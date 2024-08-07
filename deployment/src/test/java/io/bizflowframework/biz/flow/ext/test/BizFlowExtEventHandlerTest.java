package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.AggregateRootRepository;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.CreatedAt;
import io.bizflowframework.biz.flow.ext.runtime.eventsourcing.incrementer.DefaultAggregateVersionIncrementer;
import io.bizflowframework.biz.flow.ext.test.event.*;
import io.bizflowframework.biz.flow.ext.test.query.HandleTodoCreatedEventHandler;
import io.bizflowframework.biz.flow.ext.test.query.QueryEntity;
import io.bizflowframework.biz.flow.ext.test.query.QueryService;
import io.quarkus.test.QuarkusUnitTest;
import jakarta.inject.Inject;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.*;

public class BizFlowExtEventHandlerTest {
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
                    .addClass(TodoAggregateBaseJdbcPostgresqlEventRepository.class)
                    .addClass(TodoAggregateRootRepository.class)
                    .addClass(StubbedDefaultCreatedAtProvider.class)
                    .addClass(HandleTodoCreatedEventHandler.class) // here
                    .addClass(QueryEntity.class)
                    .addClass(QueryService.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"));

    @Inject
    AggregateRootRepository<TodoId, TodoAggregateRoot> todoAggregateRootRepository;

    @Inject
    StubbedDefaultCreatedAtProvider stubbedDefaultCreatedAtProvider;

    @Inject
    DefaultAggregateVersionIncrementer defaultAggregateVersionIncrementer;

    @Inject
    QueryService queryService;

    @Test
    public void shouldEventBeenExecuted() {
        // Given
        stubbedDefaultCreatedAtProvider.addResponse(new CreatedAt(LocalDateTime.of(1983, Month.JULY, 27, 19, 30)));
        final TodoAggregateRoot givenTodoAggregateRoot = new TodoAggregateRoot(new TodoId("shouldEventBeenExecuted"),
                stubbedDefaultCreatedAtProvider, defaultAggregateVersionIncrementer);
        givenTodoAggregateRoot.createNewTodo("lorem ipsum dolor sit amet");

        // When
        todoAggregateRootRepository.save(givenTodoAggregateRoot);

        // Then
        assertThat(queryService.getByTodoId(new TodoId("shouldEventBeenExecuted"))).isEqualTo(new QueryEntity(
                new TodoId("shouldEventBeenExecuted"),
                "lorem ipsum dolor sit amet",
                TodoStatus.IN_PROGRESS,
                0
        ));
    }

    @Disabled
    @Test
    public void shouldFailWhenSerdeDoesNotExistForEvent() {
        // Cannot test because the validation prevent to have an event without a Serde associated
//        // Given
//        stubbedDefaultCreatedAtProvider.addResponse(new CreatedAt(LocalDateTime.of(1983, Month.JULY, 27, 19, 30)));
//        final TodoAggregateRoot givenTodoAggregateRoot = new TodoAggregateRoot(new TodoId("shouldEventBeenExecuted"),
//                stubbedDefaultCreatedAtProvider, stubbedDefaultAggregateVersionIncrementer);
//        givenTodoAggregateRoot.createNewTodo("lorem ipsum dolor sit amet");
//        final TodoAggregateRoot saved = todoAggregateRootRepository.save(givenTodoAggregateRoot);
//
//        saved.markTodoAsCompleted();
//
//        // When && Then
//        assertThatThrownBy(() -> todoAggregateRootRepository.save(saved));
    }
}
