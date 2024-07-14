package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.test.event.TodoCreatedEvent;
import io.bizflowframework.biz.flow.ext.test.event.TodoCreatedEventSerde;
import io.quarkus.test.QuarkusUnitTest;
import jakarta.inject.Inject;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class BizFlowExtSerdeTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(TodoAggregateRoot.class)
                    .addClass(TodoId.class)
                    .addClass(TodoStatus.class)
                    .addClass(TodoCreatedEvent.class)
                    .addClass(TodoCreatedEventSerde.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"));

    @Inject
    TodoCreatedEventSerde todoCreatedEventSerde;

    @Test
    public void shouldReturnAggregateRootClass() {
        assertThat(todoCreatedEventSerde.aggregateRootClass())
                .isEqualTo(TodoAggregateRoot.class);
    }

    @Test
    public void shouldReturnAggregateRootEventPayloadClass() {
        assertThat(todoCreatedEventSerde.aggregateRootEventPayloadClass())
                .isEqualTo(TodoCreatedEvent.class);
    }
}
