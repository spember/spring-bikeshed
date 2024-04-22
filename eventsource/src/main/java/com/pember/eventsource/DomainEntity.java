package com.pember.eventsource;

import com.pember.eventsource.errors.EventOutOfOrderException;
import com.pember.eventsource.errors.UnknownEventException;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Represents a Domain Object with a Lifecycle that we wish to back or 'source' with events. The concept is derived from
 * the Domain Driven Design notion of what an Entity is.
 * <p>
 * An Entity requires a specific {@link EntityId}. These ids are also the link or anchor for a stream of events within
 * our EventLedger. We give the Ids concrete wrapper classes to distinguish them from mere Strings or UUIDs.
 * <p>
 * Entities should have no setters but rather receive Events in order to mutate state. Furthermore, developers should
 * strive to make all objects within their system to be *Immutable*, leaving DomainEntities to be the sole mutable
 * objects.
 * <p>
 * Entities are different from the DDD concept of Aggregates. An Aggregate may be modeled within your system as multiple
 * Entities linked together via some relation. We leave Aggregate up to the developer as it's not especially the concern
 * of this library; rather, we are focused on tracking events per entity.
 */
public abstract class DomainEntity<I extends EntityId<?>> {

    private final I id;
    private int revision = 0;
    private boolean active = true;


    /**
     * Constructing a DomainEntity requires a {@link EntityId}
     *
     * @param id an id of type I
     */
    public DomainEntity(@Nonnull I id) {
        this.id = id;
    }

    /**
     * Fetch the current {@link EntityId}.
     *
     * @return the id
     */
    public I getId() {
        return id;
    }

    /**
     * Fetches the revision that the DomainEntity is currently at.
     *
     * @return the revision
     */
    public int getRevision() {
        return revision;
    }

    /**
     * By 'active' we mean to signify if the Domain has been soft-deleted or otherwise rendered unusable.
     *
     * @return whether the entity is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * The main entrypoint for applying an {@link Event} to a DomainEntity, by using an {@link EventEnvelope}.
     *
     * @param eventEnvelope the EventEnvelope containing the event to apply to our Entity
     * @param <E> The Event Type
     * @throws UnknownEventException when an event is received which we cannot process/handle
     * @throws EventOutOfOrderException when an event is received whose revision number is not the next in sequence
     */
    public <E extends Event> void apply(@Nonnull EventEnvelope<I, E> eventEnvelope) throws UnknownEventException,
            EventOutOfOrderException {
        // this method is the main entry point for applying events. It checks that events are received in order, and
        // then passes the event data to a consumer required by the abstract class
        if (eventEnvelope.getRevision() != this.getRevision()+1) {
            throw new EventOutOfOrderException(this.getRevision()+1, eventEnvelope.getRevision());
        }

        if (!reactToIncomingEvent(eventEnvelope)) {
            throw new UnknownEventException(eventEnvelope.getEvent());
        }

        this.revision = eventEnvelope.getRevision();
    }

    /**
     * A convenience function, particularly useful in Java, which cuts down on the boiler plate code when checking for
     * the type of event that's being passed to a Domain Entity.
     *
     * Essentially, usages of :
     * <pre>{@code
     * if (eventEnvelope.getEvent().getClass() == SomeEvent.class {
     *     this.handle((SomeEvent)eventEnvelope.getEvent().getClass()
     *     return true
     * } else {
     *     return false
     * }
     * }</pre>
     * (perhaps the return values could be omitted) instead becomes:
     *
     * <pre>{@code
     * doIf(eventEnvelope, SomeEvent.class, {envelope -> handle(envelope)}
     * }</pre>
     *
     * The consumer receives the Envelope in a type safe fashion, no need to cast a second time before the `handle`
     * call.
     *
     * @param envelope the {@link EventEnvelope} our domain entity is reacting to
     * @param eventClass the {@link Event } class we're checking against
     * @param handler if the eventClass matches the event contained in the envelope, this code will be executed
     * @return a boolean for whether or not the event was handled this time
     * @param <E> Generic for the Event in the class and handler, ensuring type safety between the two
     */
    @SuppressWarnings("unchecked")
    protected  <E extends Event> boolean doIf(
            @Nonnull EventEnvelope<I, ? extends Event> envelope,
            @Nonnull Class<E> eventClass,
            @Nonnull Consumer<E> handler
    ) {
        if (eventClass == envelope.getEvent().getClass()) {
            handler.accept((E)envelope.getEvent());
            return true;
        }
        return false;
    }

    /**
     * A method implemented by each domain class. When an event is handed to your class, what should you do with it?
     * Can your domain understand it? Should it? Likely.
     *
     * The most straightforward code here involves an if/else class check against events you know how to handle and
     * the event in the {@link EventEnvelope}.
     *
     * @param eventEnvelope the event plus wrapper (The {@link EventEnvelope}
     * @return a boolean for whether the event was handled or not
     */
    protected abstract boolean reactToIncomingEvent(@Nonnull EventEnvelope<I, ? extends Event> eventEnvelope);

}
