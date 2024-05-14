package com.pember.bikeshed.sql

import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.projections.ProjectionOrchestrator
import com.pember.bikeshed.core.users.User
import com.pember.bikeshed.core.users.UserPersistenceOrchestrator
import com.pember.eventsource.EntityWithEvents
import org.jooq.DSLContext

class JooqUserPersistenceOrchestrator(
    private val dslContext: DSLContext,
    private val jooqEventRepository: JooqEventRepository,
    private val projectionOrchestrator: JooqProjectionOrchestrator
): UserPersistenceOrchestrator {

    override fun storeNewUser(entityWithEvents: EntityWithEvents<UserId, User>) {
        dslContext.transaction { trx ->
            jooqEventRepository.withTx(trx).persist(entityWithEvents.uncommittedEvents)

            entityWithEvents.uncommittedEvents.forEach { event ->
                projectionOrchestrator.receiveEvent(trx, event)
            }
        }
    }
}