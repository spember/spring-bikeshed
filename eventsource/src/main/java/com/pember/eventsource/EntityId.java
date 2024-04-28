package com.pember.eventsource;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A Wrapper class which specifies an Identifier for an Entity. The goal is give meaning and provide structure around
 * entity identifiers, a core underlying concept. While requiring a little more work this concept provides more safe
 * guards than using simple primitives.
 * <p>
 * For example, imagine all of our entities use a UUID identifier and we have a very expensive function:
 * <p>
 * public boolean takeMoneyFrom(UUID user, UUID account, Long value)
 * <p>
 * There is nothing stopping an engineer from swapping the account and user ids, which wouldn't be caught until run time
 * (or sufficient testing of course). However, contrast that with:
 * <p>
 * public boolean takeMoneyFrom(UserId user, AccountId account, Long value)
 * <p>
 * Now it is programattically impossible to make the mistake.
 *
 * @param <T> The underlying type of the identifier. An int, a string, an UUID, etc.
 */
public abstract class EntityId<T> {

    private final T value;

    /**
     * Main Constructor. Requires a value of type T.
     *
     * @param value the value, T, that our class will wrap.
     */
    public EntityId(@Nonnull final T value) {
        Objects.requireNonNull(value, "EntityId value cannot be null");
        this.value = value;
    }

    /**
     * Retrieve the actual, non wrapped, entity id value.
     *
     * @return the id value.
     */
    public @Nonnull T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityId<?> that = (EntityId<?>) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
