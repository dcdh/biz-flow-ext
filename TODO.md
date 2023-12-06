TODO

Aggregate
AggregateRepository<T>

MaterialState
MaterialStateSerde

EventPublisher
KafkaEventPublisher

Event
EventSerde<>
AggregateSerde<>

MaterialState = Aggregate + Event ??? je ne sais plus ...

generer automatiquement les implementations pour l'aggregat repository !!!
interface generic ... implementation auto si non existante ...

je devrais avoir une classe pour recuperer les events
pour appliquer la serialization
pour charger

pour postgresl checker que les versions se suivent via une procedure stockée !!!