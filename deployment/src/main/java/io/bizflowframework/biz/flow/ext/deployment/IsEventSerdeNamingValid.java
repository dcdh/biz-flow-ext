package io.bizflowframework.biz.flow.ext.deployment;

import java.util.function.Predicate;

public record IsEventSerdeNamingValid() implements Predicate<AggregateRootEventPayloadSerdeDescriptor> {

    @Override
    public boolean test(final AggregateRootEventPayloadSerdeDescriptor descriptor) {
        return descriptor.aggregateRootEventPayloadSerde().getSimpleName().equals(expectedNaming(descriptor));
    }

    public static String expectedNaming(final AggregateRootEventPayloadSerdeDescriptor descriptor) {
        return descriptor.eventPayloadClass().getSimpleName() + "Serde";
    }
}
