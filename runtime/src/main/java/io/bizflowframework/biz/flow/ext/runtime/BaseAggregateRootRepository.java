package io.bizflowframework.biz.flow.ext.runtime;

import io.bizflowframework.biz.flow.ext.runtime.creational.AggregateRootInstanceCreator;
import io.bizflowframework.biz.flow.ext.runtime.event.EventRepository;
import io.bizflowframework.biz.flow.ext.runtime.serde.MissingSerdeException;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Objects;

public abstract class BaseAggregateRootRepository<ID extends AggregateId, T extends AggregateRoot<ID, T>> implements AggregateRootRepository<ID, T> {
    private final EventRepository<ID, T> eventRepository;
    private final AggregateRootInstanceCreator aggregateRootInstanceCreator;

    public BaseAggregateRootRepository(final EventRepository<ID, T> eventRepository,
                                       final AggregateRootInstanceCreator aggregateRootInstanceCreator) {
        this.eventRepository = Objects.requireNonNull(eventRepository);
        this.aggregateRootInstanceCreator = Objects.requireNonNull(aggregateRootInstanceCreator);
    }

    @Override
    @Transactional
    public T save(final T aggregateRoot) throws MissingSerdeException, EventStoreException {
        Objects.requireNonNull(aggregateRoot);
        while (aggregateRoot.hasDomainEvent()) {
            final AggregateRootDomainEvent<ID, T> domainEventToSave = aggregateRoot.consumeDomainEvent();
            eventRepository.save(domainEventToSave);
        }
        return aggregateRoot;
    }

    @Override
    @Transactional
    public T load(final ID aggregateId) throws UnknownAggregateRootException, MissingSerdeException, EventStoreException {
        Objects.requireNonNull(aggregateId);
        final T instance = aggregateRootInstanceCreator.createNewInstance(clazz(), aggregateId);
        final List<AggregateRootDomainEvent> aggregateRootEvents = eventRepository.loadOrderByVersionASC(instance.aggregateRootIdentifier());
        if (aggregateRootEvents.size() == 0) {
            throw new UnknownAggregateRootException(instance.aggregateRootIdentifier());
        }
        instance.loadFromHistory(aggregateRootEvents);
        return instance;
    }

    @Override
    @Transactional
    public T load(final ID aggregateId, final AggregateVersion aggregateVersion)
            throws UnknownAggregateRootException, UnknownAggregateRootAtVersionException, MissingSerdeException, EventStoreException {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(aggregateVersion);
        final T instance = aggregateRootInstanceCreator.createNewInstance(clazz(), aggregateId);
        final List<AggregateRootDomainEvent> aggregateRootEvents = eventRepository.loadOrderByVersionASC(instance.aggregateRootIdentifier(), aggregateVersion);
        if (aggregateRootEvents.size() == 0) {
            throw new UnknownAggregateRootException(instance.aggregateRootIdentifier());
        }
        instance.loadFromHistory(aggregateRootEvents);
        if (!instance.aggregateVersion().equals(aggregateVersion)) {
            throw new UnknownAggregateRootAtVersionException(instance.aggregateRootIdentifier(), aggregateVersion);
        }
        return instance;
    }

    protected abstract Class<T> clazz();
}
