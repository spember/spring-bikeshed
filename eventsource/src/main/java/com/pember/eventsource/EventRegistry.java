package com.pember.eventsource;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.reflections.scanners.Scanners.SubTypes;

/**
 * An optional, but convenient tool. Can be used by implementors to maintain a bidirectional lookup of
 * {@link Event}s to aliases. These aliases are set by annotating Event classes with the {@link EventAlias} annotation.
 */
public class EventRegistry {

    private final Map<Class<? extends Event>, List<String>> classToAliases = new HashMap<>();
    private final Map<String, Class<? extends Event>> aliasToClass = new HashMap<>();

    private static Logger log = LoggerFactory.getLogger(EventRegistry.class);

    /**
     * Scans in a provided package for all classes that implement {@link Event}, building a reverse lookup of the
     * aliases. Will utilize {@link EventAlias} if present. The 'last' alias will be used for keys, and if no alias
     * is present the classname will be used.
     * <p>
     * An alias is recommended, and as such a warning will be logged for events without one.
     *
     * @param packageName a String for the package name to search under
     */
    public void scan(@Nonnull final String packageName) {
        Reflections reflections = new Reflections(packageName);

        Set<Class<?>> subTypes =
                reflections.get(SubTypes.of(Event.class).asClass());
        log.info("Detected " + subTypes.size() +" in package " + packageName);
        subTypes.forEach((eventSubclass) -> {
            log.debug("Looking at " + eventSubclass);
            //noinspection unchecked
            handleRegistration((Class<? extends Event>) eventSubclass);
        });
        log.info("Event Class Count in Registry: " + this.getEventCount());
    }

    /**
     *
     * @return a count of the events noticed by the registry.
     */
    public int getEventCount() {
        return this.classToAliases.size();
    }

    private void handleRegistration(@Nonnull final Class<? extends Event> eventClass) {
        List<String> aliases = new ArrayList<>();
        aliases.add(eventClass.getName());

        EventAlias anno = eventClass.getAnnotation(EventAlias.class);
        if (anno == null) {
            log.warn("Event '" + eventClass.getName() +"' is not annotated with EventAlias");
        }
        else {
            aliases.addAll(Arrays.stream(anno.value()).toList());
        }

        this.classToAliases.put(eventClass, aliases);
        aliases.forEach(alias -> {
            this.aliasToClass.put(alias, eventClass);
        });
    }


    /**
     * Retrieves the 'Alias' for a given event Class. This will be used when persisting or emitting an event, and we
     * do not want to use the default Class Name of the event Class (which may change over time)
     *
     * @param clz the Event class to look up
     * @return an Optional for whether we found an Alias in the registry or not. Note that while theoretically possible,
     * it would be highly unlikely for the response to be empty
     */
    public Optional<String> getAliasForClass(@Nonnull final Class<? extends Event> clz) {
        List<String> aliases = classToAliases.getOrDefault(clz, new ArrayList<>());
        if (aliases.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(aliases.get(aliases.size()-1));
        }
    }

    /**
     * Given an event Alias - say, right when the event data is loaded from a datastore - use this method to retrieve
     * the corresponding class it should be cast into.
     *
     * @param alias the string value that has been persisted into some data store
     * @return the Class, if any, for a class existing in Classpath which maps to that alias.
     */
    public Optional<Class<? extends Event>> getClassForAlias(@Nonnull final String alias) {
        return Optional.ofNullable(aliasToClass.get(alias));
    }
}
