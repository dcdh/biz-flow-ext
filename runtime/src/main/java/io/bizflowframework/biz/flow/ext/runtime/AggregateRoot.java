package io.bizflowframework.biz.flow.ext.runtime;

import io.bizflowframework.biz.flow.ext.runtime.event.AggregateRootEventPayload;
import io.bizflowframework.biz.flow.ext.runtime.incrementer.AggregateVersionIncrementer;
import org.apache.commons.lang3.Validate;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AggregateRoot<ID extends AggregateId, T extends AggregateRoot<ID, T>> implements Serializable {
    private final transient Queue<AggregateRootDomainEvent<ID, T>> unsavedAggregateRootDomainEvents = new ConcurrentLinkedQueue<>();
    protected final AggregateRootIdentifier<ID> aggregateRootIdentifier;
    private AggregateVersion aggregateVersion = new AggregateVersion();
    private final transient CreatedAtProvider createdAtProvider;
    private final transient AggregateVersionIncrementer aggregateVersionIncrementer;

    public AggregateRoot(final ID aggregateId,
                         final CreatedAtProvider createdAtProvider,
                         final AggregateVersionIncrementer aggregateVersionIncrementer) {
        this.aggregateRootIdentifier = new AggregateRootIdentifier<>(
                aggregateId,
                new AggregateType(this));
        this.createdAtProvider = Objects.requireNonNull(createdAtProvider);
        this.aggregateVersionIncrementer = Objects.requireNonNull(aggregateVersionIncrementer);
    }

    public AggregateRoot(final ID aggregateId,
                         final AggregateVersion aggregateVersion,
                         final CreatedAtProvider createdAtProvider,
                         final AggregateVersionIncrementer aggregateVersionIncrementer) {
        this(aggregateId, createdAtProvider, aggregateVersionIncrementer);
        this.aggregateVersion = Objects.requireNonNull(aggregateVersion);
    }

    protected final void apply(final AggregateRootEventPayload aggregateRootEventPayload) {
        Objects.requireNonNull(aggregateRootEventPayload);
        aggregateRootEventPayload.apply(this);
        aggregateVersion = aggregateVersionIncrementer.increment(aggregateVersion);
        final AggregateRootDomainEvent<ID, T> appliedAggregateRootDomainEvent = new AggregateRootDomainEvent<>(
                aggregateRootIdentifier,
                aggregateVersion,
                createdAtProvider.now(),
                aggregateRootEventPayload);
        this.unsavedAggregateRootDomainEvents.add(appliedAggregateRootDomainEvent);
    }

    final void loadFromHistory(final List<AggregateRootDomainEvent> aggregateRootDomainEvents) {
        Objects.requireNonNull(aggregateRootDomainEvents);
        Validate.validState(aggregateVersion.isUninitialized(), "Only un initialized aggregate root can be loaded from history !");
        Validate.validState(aggregateRootDomainEvents.stream()
                        .map(AggregateRootDomainEvent::aggregateRootIdentifier)
                        .allMatch(this.aggregateRootIdentifier::equals),
                "At least one aggregate root domain event does not match this aggregate !");
        aggregateRootDomainEvents.forEach(event -> {
            event.payload().apply(this);
            aggregateVersion = event.aggregateVersion();
        });
    }

    final boolean hasDomainEvent() {
        return !unsavedAggregateRootDomainEvents.isEmpty();
    }

    final AggregateRootDomainEvent<ID, T> consumeDomainEvent() {
        return unsavedAggregateRootDomainEvents.poll();
    }

    public final AggregateVersion aggregateVersion() {
        return aggregateVersion;
    }

    public final AggregateRootIdentifier<ID> aggregateRootIdentifier() {
        return aggregateRootIdentifier;
    }

    public final ID aggregateId() {
        return aggregateRootIdentifier.aggregateId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateRoot<?, ?> that = (AggregateRoot<?, ?>) o;
        return Objects.equals(aggregateRootIdentifier, that.aggregateRootIdentifier)
               && Objects.equals(aggregateVersion, that.aggregateVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootIdentifier, aggregateVersion);
    }

    @Override
    public String toString() {
        return "AggregateRoot{" +
               "unsavedAggregateRootDomainEvents=" + unsavedAggregateRootDomainEvents +
               ", aggregateRootIdentifier=" + aggregateRootIdentifier +
               ", aggregateVersion=" + aggregateVersion +
               '}';
    }
}
