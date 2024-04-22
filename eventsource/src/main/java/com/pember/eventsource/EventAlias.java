package com.pember.eventsource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies an 'alias' for an {@link Event}. The aliasing is a future proofed situation for when an Event changes
 * its name or location (e.g. it moves packages). Used by the EventRegistry, which scans the classpath for events.
 * A warning will be issued for any event which does not have an Alias. Only one alias is allowed in the registry,
 * and conflicts will throw exceptions.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EventAlias {
    /**
     * The list of string aliases tracked by this annotation.
     *
     * @return the aliases as a string array
     */
    String[] value() default "";
}
