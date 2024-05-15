package com.pember.bikeshed.sql

import com.pember.bikeshed.db.jooq.Tables.EVENT_JOURNAL
import com.fasterxml.jackson.databind.ObjectMapper
import com.pember.bikeshed.db.jooq.tables.records.EventJournalRecord
import com.pember.eventsource.EntityId
import com.pember.eventsource.Event
import com.pember.eventsource.EventEnvelope
import com.pember.eventsource.EventRegistry
import com.pember.eventsource.EventRepository
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.JSONB
import java.time.OffsetDateTime
import java.time.ZoneOffset


class JooqEventRepository(
    private val jooq: DSLContext,
    private val objectMapper: ObjectMapper,
    private val eventRegistry: EventRegistry
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


    override fun <EI : EntityId<String>> persist(envelopes: List<EventEnvelope<EI, Event>>) {
        val stmt = jooq.insertInto(EVENT_JOURNAL)
            .columns(
                EVENT_JOURNAL.ENTITY_ID,
                EVENT_JOURNAL.REVISION,
                EVENT_JOURNAL.AGENT,
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
                eventRegistry.getAliasForClass(envelope.event::class.java).get(),
                OffsetDateTime.ofInstant(envelope.timeOccurred, ZoneOffset.UTC),
                OffsetDateTime.ofInstant(envelope.timeObserved, ZoneOffset.UTC),
                JSONB.jsonb(objectMapper.writeValueAsString(envelope.event))
            )
        }
        stmt.execute()
    }

    override fun <EI : EntityId<String>> loadForId(entityId: EI): List<EventEnvelope<EI, Event>> {
        // first find most recent snapshot for entity
        return jooq.selectFrom(EVENT_JOURNAL)
            .where(EVENT_JOURNAL.ENTITY_ID.eq(entityId.value))
            .orderBy(EVENT_JOURNAL.REVISION.asc())
            .fetch()
            .map { record -> convert(entityId, record) }.toList()
    }

    override fun <EI : EntityId<String>> loadForIds(entityIds: MutableList<EI>): List<EventEnvelope<EI, Event>> {
        val lookup: MutableMap<String, EI> = mutableMapOf()
        entityIds.forEach { lookup[it.value] = it }
        return jooq.selectFrom(EVENT_JOURNAL)
            .where(EVENT_JOURNAL.ENTITY_ID.`in`(entityIds.map { it.value }))
            .orderBy(EVENT_JOURNAL.REVISION.asc())
            .fetch()
            .map { record -> convert(lookup[record.entityId]!!, record) }.toList()
    }

    override fun <EI : EntityId<String>> loadForIdAndRevision(
        entityId: EI,
        revision: Int
    ): List<EventEnvelope<EI, Event>> {
        TODO("Not yet implemented")
    }

    private fun <EI : EntityId<String>> convert(entityId:EI, record: EventJournalRecord): EventEnvelope<EI, Event> =
        EventEnvelope(
            // because our query used the original id, just slap it back in there
            entityId,
            record.revision,
            record.agent,
            record.timeOccurred.toInstant(),
            record.timeObserved.toInstant(),
            objectMapper.readValue(record.data.data(), eventRegistry.getClassForAlias(record.eventType).get())
        )

    override fun <EI : EntityId<String>> countEventsForId(entityId: EI ): Int {
        return jooq.selectCount()
            .from(EVENT_JOURNAL)
            .where(EVENT_JOURNAL.ENTITY_ID.eq(entityId.value))
            .fetchOne()?.value1()!!
    }

    fun withTx(tx: Configuration): JooqEventRepository {
        return JooqEventRepository(tx.dsl(), objectMapper, eventRegistry)
    }
}