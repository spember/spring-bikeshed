package com.pember.eventsource.errors;

import com.pember.eventsource.Event;

/**
 * Thrown when applying an event to an Entity that the entity does not know how to handle.
 */
public class UnknownEventException extends RuntimeException {
    /**
     *
     * @param message an arbitrary message, should include the event in question.
     */
    public UnknownEventException(String message) {
        super(message);
    }

    /**
     *
     * @param unknown the unknown event, in question.
     */
    public UnknownEventException(Event unknown) {
        super("Received unknown event " + unknown.getClass());
    }
}
