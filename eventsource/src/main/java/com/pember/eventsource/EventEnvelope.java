package com.pember.eventsource;

import javax.annotation.Nonnull;
import java.time.Instant;

/**
 * The EventEnvelope is a way to encapsulate and separate both the repated meta data common to all events (id, revision,
 * source, timeOccurred, timeObserved) and the Event itself. In this way, developers get to make simple Event classes
 * without having to repeat the boilerplate over and over of dealing with revisions and time.
 * <br>
 * The {@link EntityWithEvents} class is especially useful for generating event envelopes without much developer
 * intervention.
 * <br>
 * @param <I> The EntityId for the {@link DomainEntity} this Event belongs to
 * @param <E> The Class of the {@link Event} itself
 */
public class EventEnvelope<I extends EntityId<?>, E extends Event> {
    private final I entityId;
    private final int revision;
    // some key describing where this event originated. Will typically by the email or id of the user that triggered
    // this change
    private final String agent;
    private final Instant timeOccurred;
    private final Instant timeObserved;

    private final E event;

    /**
     * Main constructor.
     *
     * @param entityId id relating to the enveloped event
     * @param revision revision number for which this envelope handles to the entity
     * @param agent the marker for the user or system which is responsible for the change. Must, at minimum, be a user id (or a marker for 'system' if not actually done by a user).
     * @param timeOccurred the time that the change or event actually occurred. May be in the past, for example.
     * @param timeObserved the time that the current system received this event or processed the change which caused this event
     * @param event the {@link Event} class
     */
    public EventEnvelope(
            @Nonnull final I entityId,
            final int revision,
            @Nonnull final String agent,
            @Nonnull final Instant timeOccurred,
            @Nonnull final Instant timeObserved,
            @Nonnull final E event) {
        this.entityId = entityId;
        this.revision = revision;
        this.agent = agent;
        this.timeOccurred = timeOccurred;
        this.timeObserved = timeObserved;
        this.event = event;
    }

    /**
     *
     * @return the entityID
     */
    public I getEntityId() {
        return entityId;
    }

    /**
     *
     * @return an int
     */
    public int getRevision() {
        return revision;
    }

    /**
     *
     * @return a string
     */
    public String getAgent() {
        return agent;
    }

    /**
     *
     * @return an Instant
     */
    public Instant getTimeOccurred() {
        return timeOccurred;
    }

    /**
     *
     * @return an Instant
     */
    public Instant getTimeObserved() {
        return timeObserved;
    }

    /**
     *
     * @return the actual Event class.
     */
    public E getEvent() {
        return event;
    }
}
