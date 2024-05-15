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

class JooqEntityStore(
    private val dslContext: DSLContext,
    private val jooqEventRepository: JooqEventRepository,
    private val projectionOrchestrator: JooqProjectionOrchestrator
): EntityStore<Configuration>(jooqEventRepository) {

    override fun <EI : BaseShedId, DE : DomainEntity<EI>> persist(ewe: EntityWithEvents<EI, DE>) {

        dslContext.transaction { trx ->
            jooqEventRepository.withTx(trx).persist(ewe.uncommittedEvents)
            ewe.uncommittedEvents.forEach { event ->
                projectionOrchestrator.receiveEvent(trx, event)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun persistMultiple(ewes: List<EntityWithEvents<out EntityId<String>, out DomainEntity<out EntityId<String>>>>) {
        val events: List<EventEnvelope<EntityId<String>, Event>> = ewes.flatMap { it.uncommittedEvents } as List<EventEnvelope<EntityId<String>, Event>>
        dslContext.transaction { trx ->
            jooqEventRepository.withTx(trx).persist(events)
            events.forEach { event ->
                projectionOrchestrator.receiveEvent(trx, event)
            }
        }
    }
}