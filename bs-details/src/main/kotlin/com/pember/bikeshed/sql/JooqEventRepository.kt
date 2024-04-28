package com.pember.bikeshed.sql

import com.fasterxml.jackson.databind.ObjectMapper
import com.pember.bikeshed.db.jooq.tables.EventJournal
import com.pember.bikeshed.db.jooq.tables.EventJournal.EVENT_JOURNAL
import com.pember.eventsource.EntityId
import com.pember.eventsource.Event
import com.pember.eventsource.EventEnvelope
import com.pember.eventsource.EventRepository
import org.jooq.DSLContext
import org.jooq.JSONB
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.TimeZone


class JooqEventRepository(
    private val dslContext: DSLContext,
    private val objectMapper: ObjectMapper
): EventRepository<String> {

    /*
    entity_id varchar(32) not null, -- uuid is usual great
      revision int not null default 0,
      source text not null,
      event_type varchar(256) not null,
      time_occurred timestamp with time zone not null,
      time_observed timestamp with time zone not null,
      data jsonb not null
     */


    override fun <EI : EntityId<String>> persist(envelopes: MutableList<EventEnvelope<EI, Event>>) {

        val stmt = dslContext.insertInto(EVENT_JOURNAL)
            .columns(
                EVENT_JOURNAL.ENTITY_ID,
                EVENT_JOURNAL.REVISION,
                EVENT_JOURNAL.SOURCE,
                EVENT_JOURNAL.EVENT_TYPE,
                EVENT_JOURNAL.TIME_OCCURRED,
                EVENT_JOURNAL.TIME_OBSERVED,
                EVENT_JOURNAL.DATA
            )
        envelopes.forEach { envelope ->
            stmt.values(
                envelope.entityId.value,
                envelope.revision,
                envelope.agent.toString(),
                envelope.event.javaClass.simpleName,
                OffsetDateTime.ofInstant(envelope.timeOccurred, ZoneOffset.UTC),
                OffsetDateTime.ofInstant(envelope.timeObserved, ZoneOffset.UTC),
                JSONB.jsonb(objectMapper.writeValueAsString(envelope.event))
            )

        }
        stmt.execute()
    }

    override fun <EI : EntityId<String>> loadForId(entityId: EI): MutableList<EventEnvelope<EI, Event>> {
        TODO("Not yet implemented")
    }

    override fun <EI : EntityId<String>> loadForIdAndRevision(
        entityId: EI,
        revision: Int
    ): MutableList<EventEnvelope<EI, Event>> {
        TODO("Not yet implemented")
    }

    override fun <EI : EntityId<String>> countEventsForId(entityId: EI ): Int {
        return dslContext.selectCount()
            .from(EVENT_JOURNAL)
            .where(EVENT_JOURNAL.ENTITY_ID.eq(entityId.value))
            .fetchOne()?.value1()!!
    }
}