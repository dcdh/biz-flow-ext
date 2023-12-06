package io.bizflowframework.biz.flow.ext.runtime;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Objects;

@Singleton
public final class DefaultAggregateRootRepository implements AggregateRootRepository {
    private final EventRepository eventRepository;
    private final AggregateRootInstanceCreator aggregateRootInstanceCreator;

    public DefaultAggregateRootRepository(final EventRepository eventRepository,
                                          final AggregateRootInstanceCreator aggregateRootInstanceCreator) {
        this.eventRepository = Objects.requireNonNull(eventRepository);
        this.aggregateRootInstanceCreator = Objects.requireNonNull(aggregateRootInstanceCreator);
    }

    @Override
    @Transactional
    public <T extends AggregateRoot<T>> T save(final T aggregateRoot) {
        Objects.requireNonNull(aggregateRoot);
        while (aggregateRoot.hasDomainEvent()) {
            final AggregateRootDomainEvent<T> domainEventToSave = aggregateRoot.consumeDomainEvent();
            eventRepository.save(domainEventToSave);
        }
        return aggregateRoot;
    }

    @Override
    @Transactional
    public <T extends AggregateRoot<T>> T load(final AggregateId aggregateId, final Class<T> clazz)
            throws UnknownAggregateRootException {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(clazz);
        final T instance = aggregateRootInstanceCreator.createNewInstance(clazz, aggregateId);
        final List<AggregateRootDomainEvent<T>> aggregateRootEvents = eventRepository.loadOrderByVersionASC(instance.aggregateId());
        if (aggregateRootEvents.size() == 0) {
            throw new UnknownAggregateRootException(instance.aggregateId());
        }
        instance.loadFromHistory(aggregateRootEvents);
        return instance;
    }

    @Override
    @Transactional
    public <T extends AggregateRoot<T>> T load(final AggregateId aggregateId, final Class<T> clazz, final AggregateVersion aggregateVersion)
            throws UnknownAggregateRootException, UnknownAggregateRootAtVersionException {
        Objects.requireNonNull(aggregateId);
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(aggregateVersion);
        final T instance = aggregateRootInstanceCreator.createNewInstance(clazz, aggregateId);
        final List<AggregateRootDomainEvent<T>> aggregateRootEvents = eventRepository.loadOrderByVersionASC(instance.aggregateId(), aggregateVersion);
        if (aggregateRootEvents.size() == 0) {
            throw new UnknownAggregateRootException(instance.aggregateId());
        }
        instance.loadFromHistory(aggregateRootEvents);
        if (!instance.aggregateVersion().equals(aggregateVersion)) {
            throw new UnknownAggregateRootAtVersionException(instance.aggregateId(), instance.aggregateVersion());
        }
        return instance;
    }
}
