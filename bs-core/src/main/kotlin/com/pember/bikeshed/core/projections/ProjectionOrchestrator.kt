package com.pember.bikeshed.core.projections

import com.pember.bikeshed.core.BaseShedId
import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.bikes.BikeAddedToInventory
import com.pember.bikeshed.core.bikes.BikeRented
import com.pember.bikeshed.core.bikes.BikeRetired
import com.pember.bikeshed.core.bikes.BikeReturned
import com.pember.bikeshed.core.bikes.HeldForRepairs
import com.pember.bikeshed.core.bikes.RepairsCompleted
import com.pember.bikeshed.core.reservations.BikesRemovedFromReservation
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
            // bikes made available again
            is BikeAddedToInventory -> makeBikeAvailable(transactionalClient, eventEnvelope.entityId as BikeId, eventEnvelope.revision)
            is BikeReturned -> makeBikeAvailable(transactionalClient, eventEnvelope.entityId as BikeId, eventEnvelope.revision)
            is BikesRemovedFromReservation -> makeBikeAvailable(transactionalClient, eventEnvelope.entityId as BikeId, eventEnvelope.revision)
            is RepairsCompleted -> makeBikeAvailable(transactionalClient, eventEnvelope.entityId as BikeId, eventEnvelope.revision)
            // bikes no longer available
            is BikeRented -> makeBikeUnavailable(transactionalClient, eventEnvelope.entityId as BikeId, eventEnvelope.revision)
            is BikeRetired-> makeBikeUnavailable(transactionalClient, eventEnvelope.entityId as BikeId, eventEnvelope.revision)
            is HeldForRepairs -> makeBikeUnavailable(transactionalClient, eventEnvelope.entityId as BikeId, eventEnvelope.revision)

            else -> {
                log.debug("Received event not handled by projection orchestrator: $event")
            }
        }
    }


    abstract fun updateUserLookup(transactionalClient: TX, eventEnvelope: EventEnvelope<UserId, UserCreated>)
    abstract fun updateUserRole(transactionalClient: TX, eventEnvelope: EventEnvelope<UserId, RoleChanged>)

    abstract fun makeBikeAvailable(transactionalClient: TX, bikeId: BikeId, revision: Int)

    abstract fun makeBikeUnavailable(transactionalClient: TX, bikeId: BikeId, revision: Int)

    companion object {
        private val log = LoggerFactory.getLogger(ProjectionOrchestrator::class.java)
    }
}