package com.pember.bikeshed.sql

import com.fasterxml.jackson.databind.ObjectMapper
import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.users.User
import com.pember.bikeshed.core.users.UserPersistenceOrchestrator
import com.pember.eventsource.EntityWithEvents
import com.pember.eventsource.EventRegistry
import org.jooq.DSLContext

class JooqUserPersistenceOrchestrator(
    private val dslContext: DSLContext,
    private val eventRegistry: EventRegistry,
    private val objectMapper: ObjectMapper
): UserPersistenceOrchestrator {

    override fun storeNewUser(entityWithEvents: EntityWithEvents<UserId, User>) {
        dslContext.transaction { trx ->
            JooqUserConstraintsRepository(trx.dsl()).updateUserConstraints(
                entityWithEvents.entity.id,
                entityWithEvents.entity.email,
                entityWithEvents.entity.isEmployee
            )
            JooqEventRepository(trx.dsl(),  objectMapper, eventRegistry).persist(entityWithEvents.uncommittedEvents)
        }
    }
}