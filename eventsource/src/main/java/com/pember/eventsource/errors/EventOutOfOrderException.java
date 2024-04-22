package com.pember.eventsource.errors;

import com.pember.eventsource.DomainEntity;

/**
 * An Exception signalling that a {@link DomainEntity} has received an event out-of-order, or
 * without an unexpected revision number. For example, if the entity were at revision 5 and were to receive/apply an
 * event at revision 10, this error will be thrown (expected 6).
 */
public class EventOutOfOrderException extends RuntimeException{

    /**
     * Constructor with a simple message.
     *
     * @param message The message to raise
     */
    public EventOutOfOrderException(String message) {
        super(message);
    }

    /**
     *
     * @param expected the int of the expected revision
     * @param actual the actual revision
     */
    public EventOutOfOrderException(int expected, int actual) {
        super("Expected to receive event with revision " + expected + "but instead received " + actual);
    }
}
