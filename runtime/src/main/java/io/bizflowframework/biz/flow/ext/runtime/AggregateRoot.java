package io.bizflowframework.biz.flow.ext.runtime;

import org.apache.commons.lang3.Validate;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AggregateRoot<SELF extends AggregateRoot<SELF>> {
    private final transient Queue<AggregateRootDomainEvent<SELF>> unsavedAggregateRootDomainEvents = new ConcurrentLinkedQueue<>();
    private final AggregateId aggregateId;
    private final AggregateType aggregateType;
    private AggregateVersion aggregateVersion = new AggregateVersion();
    private final transient CreatedAtProvider createdAtProvider;

    public AggregateRoot(final AggregateId aggregateId,
                         final CreatedAtProvider createdAtProvider) {
        this.aggregateId = Objects.requireNonNull(aggregateId);
        this.aggregateType = new AggregateType(this);
        this.createdAtProvider = Objects.requireNonNull(createdAtProvider);
    }

    public AggregateRoot(final AggregateId aggregateId,
                         final AggregateVersion aggregateVersion,
                         final CreatedAtProvider createdAtProvider) {
        this(aggregateId, createdAtProvider);
        this.aggregateVersion = Objects.requireNonNull(aggregateVersion);
    }

    @SuppressWarnings("unchecked")
    protected final void apply(final AggregateRootEventPayload<SELF> aggregateRootEventPayload) {
        Objects.requireNonNull(aggregateRootEventPayload);
        aggregateRootEventPayload.apply((SELF) this);
        aggregateVersion = aggregateVersion.increment();
        final AggregateRootDomainEvent<SELF> appliedAggregateRootDomainEvent = new AggregateRootDomainEvent<>(
                aggregateId,
                aggregateType,
                aggregateVersion,
                createdAtProvider.now(),
                aggregateRootEventPayload);
        this.unsavedAggregateRootDomainEvents.add(appliedAggregateRootDomainEvent);
    }

    @SuppressWarnings("unchecked")
    final void loadFromHistory(final List<AggregateRootDomainEvent<SELF>> aggregateRootDomainEvents) {
        Objects.requireNonNull(aggregateRootDomainEvents);
        Validate.validState(aggregateVersion.isUninitialized(), "Aggregate Root already loaded from history");
        Validate.validState(aggregateRootDomainEvents.stream().allMatch(aggregateRootEvent -> aggregateId.equals(aggregateRootEvent.aggregateId())),
                "Aggregate root id and event aggregate root id mismatch");
        Validate.validState(aggregateRootDomainEvents.stream().allMatch(aggregateRootEvent -> aggregateType.equals(aggregateRootEvent.aggregateType())),
                "Aggregate root type and event aggregate root type mismatch");
        aggregateRootDomainEvents.forEach(event -> {
            event.payload().apply((SELF) this);
            aggregateVersion = event.aggregateVersion();
        });
    }

    final boolean hasDomainEvent() {
        return !unsavedAggregateRootDomainEvents.isEmpty();
    }

    final AggregateRootDomainEvent<SELF> consumeDomainEvent() {
        return unsavedAggregateRootDomainEvents.poll();
    }

    public final AggregateVersion aggregateVersion() {
        return aggregateVersion;
    }

    public final AggregateId aggregateId() {
        return aggregateId;
    }

    public final AggregateType aggregateType() {
        return aggregateType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateRoot<?> that = (AggregateRoot<?>) o;
        return Objects.equals(unsavedAggregateRootDomainEvents, that.unsavedAggregateRootDomainEvents)
               && Objects.equals(aggregateId, that.aggregateId)
               && Objects.equals(aggregateType, that.aggregateType)
               && Objects.equals(aggregateVersion, that.aggregateVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unsavedAggregateRootDomainEvents, aggregateId, aggregateType, aggregateVersion);
    }
}
