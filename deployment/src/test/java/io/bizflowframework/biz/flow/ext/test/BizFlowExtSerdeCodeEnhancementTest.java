package io.bizflowframework.biz.flow.ext.test;

import io.bizflowframework.biz.flow.ext.test.event.*;
import io.quarkus.test.QuarkusUnitTest;
import jakarta.inject.Inject;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

public class BizFlowExtSerdeCodeEnhancementTest {
    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                            .addClass(TodoAggregateRoot.class)
                            .addClass(TodoId.class)
                            .addClass(TodoStatus.class)
                            .addClass(TodoCreated.class)
                            .addClass(TodoMarkedAsCompleted.class)
                            .addClass(TodoCreatedAggregateRootEventPayloadSerdeToEnhance.class)
                            .addClass(UnknownTodoEvent.class));

    @Inject
    TodoCreatedAggregateRootEventPayloadSerdeToEnhance todoCreatedAggregateRootEventPayloadSerdeToEnhance;

    @Test
    public void shouldReturnAggregateRootClass() {
        assertThat(todoCreatedAggregateRootEventPayloadSerdeToEnhance.aggregateRootClass())
                .isEqualTo(TodoAggregateRoot.class);
    }

    @Test
    public void shouldReturnAggregateRootEventPayloadClass() {
        assertThat(todoCreatedAggregateRootEventPayloadSerdeToEnhance.aggregateRootEventPayloadClass())
                .isEqualTo(TodoCreated.class);
    }
}
