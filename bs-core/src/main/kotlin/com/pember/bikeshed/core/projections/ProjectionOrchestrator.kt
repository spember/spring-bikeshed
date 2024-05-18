package com.pember.bikeshed.core.projections

import com.pember.bikeshed.core.BaseShedId
import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.ReservationId
import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.bikes.BikeAddedToInventory
import com.pember.bikeshed.core.bikes.BikeRented
import com.pember.bikeshed.core.bikes.BikeRetired
import com.pember.bikeshed.core.bikes.BikeReturned
import com.pember.bikeshed.core.bikes.HeldForRepairs
import com.pember.bikeshed.core.bikes.RepairsCompleted
import com.pember.bikeshed.core.reservations.BikesAddedToReservation
import com.pember.bikeshed.core.reservations.BikesRemovedFromReservation
import com.pember.bikeshed.core.reservations.ReservationBegun
import com.pember.bikeshed.core.reservations.ReservationCancelled
import com.pember.bikeshed.core.reservations.ReservationCompleted
import com.pember.bikeshed.core.reservations.ReservationOpened
import com.pember.bikeshed.core.users.RoleChanged
import com.pember.bikeshed.core.users.UserCreated

import com.pember.eventsource.Event
import com.pember.eventsource.EventEnvelope
import org.slf4j.LoggerFactory
import java.util.function.Consumer

abstract class ProjectionOrchestrator<TX> {

    /**
     * For some class of Projections, we want to have them updated synchronously at the same moment our event journal is
     * persisted. This method - in this example repository - is called by the {EntityStore} after the events have been
     * persisted to the Journal and within the same transactional boundary.
     *
     * The balance is that this method must not take too long nor execute too many calls (postgres queries, redis calls,
     * etc) otherwise we're trading for performance.
     *
     * Consider that some Projections (query models, etc) may be updated asynchronously, and we should embrace that
     * option.
     */
    @Suppress("UNCHECKED_CAST")
    fun <I: BaseShedId, E: Event> receiveEventForConstraints(transactionalClient: TX, eventEnvelope: EventEnvelope<I, E>) {

        when(val event = eventEnvelope.event) {
            is UserCreated -> updateUserLookup(transactionalClient, (eventEnvelope as EventEnvelope<UserId, UserCreated>))
            is RoleChanged -> updateUserRole(transactionalClient, (eventEnvelope as EventEnvelope<UserId, RoleChanged>))
            // bikes made available again
            is BikeAddedToInventory, is BikeReturned, is BikesRemovedFromReservation, is RepairsCompleted ->
                makeBikeAvailable(transactionalClient, eventEnvelope.entityId as BikeId, eventEnvelope.revision)
            // bikes no longer available
            is BikeRented, is BikeRetired, is HeldForRepairs -> makeBikeUnavailable(transactionalClient, eventEnvelope.entityId as BikeId, eventEnvelope.revision)

            else -> {
                log.debug("Received event not handled by projection orchestrator: " + event::class.java.simpleName)
            }
        }
    }

    /**
     * For Projections that are acceptable to be eventually consistent - which should be most of them! - this method
     * should be used to dispatch received events to the appropriate handlers.
     *
     * While our toy repository here simply uses threading, it is almost certain that you'd want to use a message queue
     * in actual deployments.
     */
    @Suppress("UNCHECKED_CAST")
    fun <I: BaseShedId, E: Event> receiveEventEventually(eventEnvelope: EventEnvelope<I, E>) {
        when(val event = eventEnvelope.event) {
            is ReservationOpened -> { asyncOpenReservation(eventEnvelope as EventEnvelope<ReservationId, ReservationOpened>) }
            is BikesAddedToReservation -> asyncAddBikes(eventEnvelope as EventEnvelope<ReservationId, BikesAddedToReservation>)
            is BikesRemovedFromReservation -> asyncRemoveBikes(eventEnvelope as EventEnvelope<ReservationId, BikesRemovedFromReservation>)
            is ReservationCancelled, is ReservationCompleted -> asyncArchiveReservation(eventEnvelope.entityId as ReservationId)
            else -> log.debug("Received event not handled by projection orchestrator: " + event::class.java.simpleName);
        }
    }


    abstract fun updateUserLookup(transactionalClient: TX, eventEnvelope: EventEnvelope<UserId, UserCreated>)
    abstract fun updateUserRole(transactionalClient: TX, eventEnvelope: EventEnvelope<UserId, RoleChanged>)

    abstract fun makeBikeAvailable(transactionalClient: TX, bikeId: BikeId, revision: Int)

    abstract fun makeBikeUnavailable(transactionalClient: TX, bikeId: BikeId, revision: Int)

    abstract fun asyncOpenReservation(eventEnvelope: EventEnvelope<ReservationId, ReservationOpened>)
    abstract fun asyncAddBikes(eventEnvelope: EventEnvelope<ReservationId, BikesAddedToReservation>)
    abstract fun asyncRemoveBikes(eventEnvelope: EventEnvelope<ReservationId, BikesRemovedFromReservation>)

    abstract fun asyncArchiveReservation(reservationId: ReservationId)

    companion object {
        private val log = LoggerFactory.getLogger(ProjectionOrchestrator::class.java)
    }
}