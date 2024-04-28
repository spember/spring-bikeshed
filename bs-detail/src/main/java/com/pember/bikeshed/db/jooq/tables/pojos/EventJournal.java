/*
 * This file is generated by jOOQ.
 */
package com.pember.bikeshed.db.jooq.tables.pojos;


import java.io.Serializable;
import java.time.OffsetDateTime;

import org.jooq.JSONB;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class EventJournal implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String entityId;
    private final Integer revision;
    private final String source;
    private final String eventType;
    private final OffsetDateTime timeOccurred;
    private final OffsetDateTime timeObserved;
    private final JSONB data;

    public EventJournal(EventJournal value) {
        this.entityId = value.entityId;
        this.revision = value.revision;
        this.source = value.source;
        this.eventType = value.eventType;
        this.timeOccurred = value.timeOccurred;
        this.timeObserved = value.timeObserved;
        this.data = value.data;
    }

    public EventJournal(
        String entityId,
        Integer revision,
        String source,
        String eventType,
        OffsetDateTime timeOccurred,
        OffsetDateTime timeObserved,
        JSONB data
    ) {
        this.entityId = entityId;
        this.revision = revision;
        this.source = source;
        this.eventType = eventType;
        this.timeOccurred = timeOccurred;
        this.timeObserved = timeObserved;
        this.data = data;
    }

    /**
     * Getter for <code>public.event_journal.entity_id</code>.
     */
    public String getEntityId() {
        return this.entityId;
    }

    /**
     * Getter for <code>public.event_journal.revision</code>.
     */
    public Integer getRevision() {
        return this.revision;
    }

    /**
     * Getter for <code>public.event_journal.source</code>.
     */
    public String getSource() {
        return this.source;
    }

    /**
     * Getter for <code>public.event_journal.event_type</code>.
     */
    public String getEventType() {
        return this.eventType;
    }

    /**
     * Getter for <code>public.event_journal.time_occurred</code>.
     */
    public OffsetDateTime getTimeOccurred() {
        return this.timeOccurred;
    }

    /**
     * Getter for <code>public.event_journal.time_observed</code>.
     */
    public OffsetDateTime getTimeObserved() {
        return this.timeObserved;
    }

    /**
     * Getter for <code>public.event_journal.data</code>.
     */
    public JSONB getData() {
        return this.data;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final EventJournal other = (EventJournal) obj;
        if (this.entityId == null) {
            if (other.entityId != null)
                return false;
        }
        else if (!this.entityId.equals(other.entityId))
            return false;
        if (this.revision == null) {
            if (other.revision != null)
                return false;
        }
        else if (!this.revision.equals(other.revision))
            return false;
        if (this.source == null) {
            if (other.source != null)
                return false;
        }
        else if (!this.source.equals(other.source))
            return false;
        if (this.eventType == null) {
            if (other.eventType != null)
                return false;
        }
        else if (!this.eventType.equals(other.eventType))
            return false;
        if (this.timeOccurred == null) {
            if (other.timeOccurred != null)
                return false;
        }
        else if (!this.timeOccurred.equals(other.timeOccurred))
            return false;
        if (this.timeObserved == null) {
            if (other.timeObserved != null)
                return false;
        }
        else if (!this.timeObserved.equals(other.timeObserved))
            return false;
        if (this.data == null) {
            if (other.data != null)
                return false;
        }
        else if (!this.data.equals(other.data))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.entityId == null) ? 0 : this.entityId.hashCode());
        result = prime * result + ((this.revision == null) ? 0 : this.revision.hashCode());
        result = prime * result + ((this.source == null) ? 0 : this.source.hashCode());
        result = prime * result + ((this.eventType == null) ? 0 : this.eventType.hashCode());
        result = prime * result + ((this.timeOccurred == null) ? 0 : this.timeOccurred.hashCode());
        result = prime * result + ((this.timeObserved == null) ? 0 : this.timeObserved.hashCode());
        result = prime * result + ((this.data == null) ? 0 : this.data.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("EventJournal (");

        sb.append(entityId);
        sb.append(", ").append(revision);
        sb.append(", ").append(source);
        sb.append(", ").append(eventType);
        sb.append(", ").append(timeOccurred);
        sb.append(", ").append(timeObserved);
        sb.append(", ").append(data);

        sb.append(")");
        return sb.toString();
    }
}
