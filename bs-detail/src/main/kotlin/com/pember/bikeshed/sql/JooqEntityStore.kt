package com.pember.bikeshed.sql

import com.pember.bikeshed.core.BaseShedId
import com.pember.bikeshed.core.common.EntityStore
import com.pember.eventsource.DomainEntity
import com.pember.eventsource.EntityWithEvents
import org.jooq.Configuration
import org.jooq.DSLContext

class JooqEntityStore(
    private val dslContext: DSLContext,
    private val jooqEventRepository: JooqEventRepository,
    private val projectionOrchestrator: JooqProjectionOrchestrator
): EntityStore<Configuration>() {
    override fun <EI : BaseShedId, DE : DomainEntity<EI>> persist(ewe: EntityWithEvents<EI, DE>) {
        dslContext.transaction { trx ->
            jooqEventRepository.withTx(trx).persist(ewe.uncommittedEvents)
            ewe.uncommittedEvents.forEach { event ->
                projectionOrchestrator.receiveEvent(trx, event)
            }
        }
    }
}