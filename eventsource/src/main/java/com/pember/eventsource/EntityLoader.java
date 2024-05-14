package com.pember.eventsource;

/**
 * Many of the basic 'load and apply' operations are exactly the same for all of our {@link DomainEntity}s.
 * This service class handles those basic common loading operations, and is effectively a small logical wrapper
 * around directly interacting with the {@link EventRepository}.
 */
public class EntityLoader<T> {

    private final EventRepository<T> eventRepository;

    public EntityLoader(EventRepository<T> eventRepository) {
        this.eventRepository = eventRepository;
    }

    public <EI extends EntityId<T>, DE extends DomainEntity<EI>> DE loadCurrentState(DE domainEntity) {
        eventRepository.loadForId(domainEntity.getId()).forEach(domainEntity::apply);
        return domainEntity;
    }
}