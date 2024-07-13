package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.test.event.TodoCreated;
import io.bizflowframework.biz.flow.ext.test.event.TodoCreatedAggregateRootEventPayloadSerde;
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
                    .addClass(TodoCreated.class)
                    .addClass(TodoCreatedAggregateRootEventPayloadSerde.class)
                    .addAsResource("application.properties")
                    .addAsResource("init.sql"));

    @Inject
    TodoCreatedAggregateRootEventPayloadSerde todoCreatedAggregateRootEventPayloadSerde;

    @Test
    public void shouldReturnAggregateRootClass() {
        assertThat(todoCreatedAggregateRootEventPayloadSerde.aggregateRootClass())
                .isEqualTo(TodoAggregateRoot.class);
    }

    @Test
    public void shouldReturnAggregateRootEventPayloadClass() {
        assertThat(todoCreatedAggregateRootEventPayloadSerde.aggregateRootEventPayloadClass())
                .isEqualTo(TodoCreated.class);
    }
}
