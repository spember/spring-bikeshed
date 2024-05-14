package com.pember.bikeshed.core.projections

import com.pember.bikeshed.core.BaseShedId
import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.users.RoleChanged
import com.pember.bikeshed.core.users.UserCreated

import com.pember.eventsource.Event
import com.pember.eventsource.EventEnvelope
import org.slf4j.LoggerFactory

abstract class ProjectionOrchestrator<TX> {

    @Suppress("UNCHECKED_CAST")
    fun <I: BaseShedId, E: Event> receiveEvent(transactionalClient: TX, eventEnvelope: EventEnvelope<I, E>) {
        log.info("Projection working on ${eventEnvelope.event}")
        when(val event = eventEnvelope.event) {
            is UserCreated -> updateUserLookup(transactionalClient, (eventEnvelope as EventEnvelope<UserId, UserCreated>))
            is RoleChanged -> updateUserRole(transactionalClient, (eventEnvelope as EventEnvelope<UserId, RoleChanged>))
            else -> {
                log.debug("Received event not handled by projection orchestrator: $event")
            }
        }
    }


    abstract fun updateUserLookup(transactionalClient: TX, eventEnvelope: EventEnvelope<UserId, UserCreated>)
    abstract fun updateUserRole(transactionalClient: TX, eventEnvelope: EventEnvelope<UserId, RoleChanged>)

    companion object {
        private val log = LoggerFactory.getLogger(ProjectionOrchestrator::class.java)
    }
}