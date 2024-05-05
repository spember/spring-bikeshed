package com.pember.eventsource;

import com.pember.eventsource.errors.EventOutOfOrderException;
import com.pember.eventsource.errors.UnknownEventException;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A convenience class for mitigating the pain of constructing events during a given transaction. Without this, a
 * developer would have to wire up timestamps, revision numbers, etc for each event applied to an Entity during a
 * given mutation. This would also involve instantiating {@link EventEnvelope}s.
 * <br>
 * With this class, it becomes nearly trivial. A matter of creating individual {@link Event} classes themselves. E.g.
 * <pre>{@code
 * Chair chair = new Chair(ChairId("sku-123"))
 * EntityWithEvents<ChairId, Chair> ewe = new EntityWithEvents(chair, "userId1234")
 * ewe.apply(
 *   ChairCreated(),
 *   LegsAdded(4),
 *   PersonSat("Bob"),
 *   PersonStood("Bob")
 * )
 * eventRepository.persist(ewe.getUncommittedEvents)
 * // and the events are now committed
 * }</pre>
 *
 */
public class EntityWithEvents<EI extends EntityId<?>, EN extends DomainEntity<EI>> {

    private final EN entity;
    private final String agent;
    // reflects when an event has occurred, typically outside of this current system.
    private final Instant timeOccurred;

    private Integer nextRevision;

    private final List<EventEnvelope<EI, Event>> uncommittedEvents = new ArrayList<>();

    /**
     * Constructor which assumes TimeOccurred is now.
     *
     * @param entity the {@link DomainEntity} to be tracked
     * @param agent the id of the person or system that is responsible for this change
     */
    public EntityWithEvents(@Nonnull EN entity, @Nonnull String agent) {
        this.entity = entity;
        this.agent = agent;
        this.timeOccurred = Instant.now();
    }

    /**
     * Constructor for when TimeOccurred is not now. Useful for when an event is received by your system in the future.
     *
     * @param entity the {@link DomainEntity} to be tracked
     * @param agent the 'source' that is responsible for this change
     * @param timeOccurred the Instant or time when this event occurred, if not now.
     */
    public EntityWithEvents(@Nonnull EN entity, @Nonnull String agent, @Nonnull Instant timeOccurred) {
        this.entity = entity;
        this.agent = agent;
        this.timeOccurred = timeOccurred;
    }

    /**
     * Retrieves the entity at its current state
     *
     * @return EN the entity
     */
    public EN getEntity() {
        return entity;
    }

    /**
     * Apply one or more events to the entity.
     *
     * @param events the events to apply
     * @return this EntityWithEvents
     */
    public EntityWithEvents<EI, EN> apply(@Nonnull Event... events) {
        return this.apply(Arrays.stream(events).toList());
    }

    /**
     * Apply a List of Events to your Entity.
     *
     * @param events the events as a List
     * @return this
     * @throws EventOutOfOrderException for when Events are applied to your underlying entity in incorrect order
     * @throws UnknownEventException when an event is received which your Entity does not know how to handle.
     */
    public EntityWithEvents<EI, EN> apply(@Nonnull List<? extends Event> events) throws EventOutOfOrderException,
            UnknownEventException {
        events.forEach(event -> {
            var envelope = new EventEnvelope<>(
                    this.entity.getId(),
                    this.getNextRevision(),
                    this.agent,
                    this.timeOccurred,
                    Instant.now(), // time observed is always .now(),
                    event
            );
            this.entity.apply(envelope);
            this.uncommittedEvents.add((EventEnvelope<EI, Event>) envelope);
        });
        return this;
    }

    /**
     * Retrieve the events which have been applied to our Entity so far. Useful for persisting or committing after
     * verifying that the Entity hasn't been broken or made invalid.
     *
     * @return the List of EventEnvelopes applied and created
     */
    public List<EventEnvelope<EI, Event>> getUncommittedEvents() {
        return uncommittedEvents;
    }

    private int getNextRevision() {
        if (this.nextRevision == null) {
            this.nextRevision = entity.getRevision();
        }
        this.nextRevision++;
        return this.nextRevision;
    }
}
