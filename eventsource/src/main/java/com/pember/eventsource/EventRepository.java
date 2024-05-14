package com.pember.eventsource;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An Event Repository for some Event Stream. Can handle multiple different entities - as long as the Entity Identifier
 * has the same underlying value - uuid, string, int, etc.
 *
 * @param <T> The underlying type of the {@link EntityId}s supported by the implementation. This means that if you have
 *            multiple underyling types, you'll need eventRepositories for each type - and potentially different event
 *            journals.
 */
public interface EventRepository<T> {

    /**
     * Persist a list of events to the event journal. Note that the received envelopes are not for any one specific
     * entity. Meaning, that it's possible - and suggested - for implementations to persist all events for a given
     * transaction in a single write, across multiple Domain Entities (if applicable).
     *
     * @param envelope the envelopes to persist
     */
    <EI extends EntityId<T>> void persist(@Nonnull final List<EventEnvelope<EI, Event>> envelope);

    /**
     * Loads all Events + Envelopes for a given Entity. Implementations must take care to return the events in order,
     * and to be aware that the resulting event set may be large - multiple queries may be needed.
     *
     * @param entityId the Identifier of the DomainEntity we want the events for
     * @param <EI>     The EntityId type
     * @return a List of Event Envelopes
     */
    <EI extends EntityId<T>> List<EventEnvelope<EI, Event>> loadForId(@Nonnull final EI entityId);
    // todo: you'll want to change these load operations to require a Supplier so that we can stream events to the Supplier in batches

    /**
     * For most day-to-day use cases, {@link #loadForId(EntityId) loadForId()} will be used most often, as it should
     * load all persisted Events for the provided Entity. However, there will occasions where it is useful to load an
     * entity at some specific revision. For example:
     * <p>
     * * rolling back an entity some number of steps ('undo')
     * * verifying the state of an entity previously, in order to compare before/after
     * * fixing mistakes / restore: see what the value of an entity was at a previous point, and then apply a corrective
     * event to restore that value.
     *
     * @param entityId The {@link EntityId} involved, the 'anchor' on which to load events
     * @param revision The maximum revision number to load. Implementations should load from 0-to-revision, inclusive
     * @param <EI>     The EntityId type
     * @return a List of Event Envelopes
     */
    <EI extends EntityId<T>> List<EventEnvelope<EI, Event>> loadForIdAndRevision(
            @Nonnull final EI entityId,
            @Nonnull final Integer revision
    );

    /**
     * Count the number of events in our Event Ledger, if any. Implementers should simply do a quick index-based lookup
     * on the entityID.
     *
     * @param entityId the id of the entity to count for
     * @param <EI>     The entityId type
     * @return the number of events within our system for a given id
     */
    <EI extends EntityId<T>> Integer countEventsForId(@Nonnull final EI entityId);


}
