package com.pember.bikeshed.sql

import com.pember.bikeshed.core.BaseShedId
import com.pember.bikeshed.core.common.EntityStore
import com.pember.eventsource.DomainEntity
import com.pember.eventsource.EntityId
import com.pember.eventsource.EntityWithEvents
import com.pember.eventsource.Event
import com.pember.eventsource.EventEnvelope
import org.jooq.Configuration
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

class JooqEntityStore(
    private val dslContext: DSLContext,
    private val jooqEventRepository: JooqEventRepository,
    private val projectionOrchestrator: JooqProjectionOrchestrator
): EntityStore<Configuration>(jooqEventRepository) {

    @Suppress("UNCHECKED_CAST")
    override fun <EI : BaseShedId, DE : DomainEntity<EI>> persist(ewe: EntityWithEvents<EI, DE>) {
        saveAndRoute(ewe.uncommittedEvents as List<EventEnvelope<EntityId<String>, Event>>)
    }

    @Suppress("UNCHECKED_CAST")
    override fun persistMultiple(ewes: List<EntityWithEvents<out EntityId<String>, out DomainEntity<out EntityId<String>>>>) {
        val events: List<EventEnvelope<EntityId<String>, Event>> = ewes.flatMap { it.uncommittedEvents } as List<EventEnvelope<EntityId<String>, Event>>
        saveAndRoute(events)
    }

    private fun saveAndRoute(events: List<EventEnvelope<EntityId<String>, Event>>) {
        dslContext.transaction { trx ->
            jooqEventRepository.withTx(trx).persist(events)
            events.forEach { event ->
                projectionOrchestrator.receiveEventForConstraints(trx, event)
            }
        }

        // in a real-world scenario we'd publish these onto a queue for delayed, eventually-consistent processing

        val work = Executors.newFixedThreadPool(1).submit {
            log.info("Dispatching events async")
            events.forEach { event ->
                try {
                    projectionOrchestrator.receiveEventEventually(event)
                } catch(e: Exception) {
                    log.error("Could not process event async", e)
                }
            }
            asyncCompleteHandler.accept(events.size)
        }
        log.info("job dispatched -> ${work.state()}")
    }

    companion object {
        private val log = LoggerFactory.getLogger(JooqEntityStore::class.java)
    }
}