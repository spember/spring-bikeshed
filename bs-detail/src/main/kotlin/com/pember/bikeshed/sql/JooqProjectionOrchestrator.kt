package com.pember.bikeshed.sql

import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.ReservationId
import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.projections.ProjectionOrchestrator
import com.pember.bikeshed.core.reservations.BikesAddedToReservation
import com.pember.bikeshed.core.reservations.BikesRemovedFromReservation
import com.pember.bikeshed.core.reservations.ReservationOpened
import com.pember.bikeshed.core.reservations.ReservationsQueryModelRepository
import com.pember.bikeshed.core.users.RoleChanged
import com.pember.bikeshed.core.users.UserCreated
import com.pember.eventsource.EventEnvelope
import org.jooq.Configuration

class JooqProjectionOrchestrator(
    private val reservationsQueryModelRepository: ReservationsQueryModelRepository
): ProjectionOrchestrator<Configuration>() {

    override fun updateUserLookup(
        transactionalClient: Configuration,
        eventEnvelope: EventEnvelope<UserId, UserCreated>
    ) {
        JooqUserConstraintsRepository(transactionalClient.dsl()).updateUserConstraints(
            eventEnvelope.entityId, eventEnvelope.event.email, false)
    }

    override fun updateUserRole(transactionalClient: Configuration, eventEnvelope: EventEnvelope<UserId, RoleChanged>) {
        JooqUserConstraintsRepository(transactionalClient.dsl())
            .updateUserRole(eventEnvelope.entityId, eventEnvelope.event.employee)
    }

    override fun makeBikeAvailable(transactionalClient: Configuration, bikeId: BikeId, revision: Int) {
        JooqBikeAvailabilityRepository(transactionalClient.dsl()).addAvailableBike(bikeId, revision)
    }

    override fun makeBikeUnavailable(transactionalClient: Configuration, bikeId: BikeId, revision: Int) {
        JooqBikeAvailabilityRepository(transactionalClient.dsl()).removeAvailableBike(bikeId, revision)
    }

    override fun asyncOpenReservation(
        eventEnvelope: EventEnvelope<ReservationId, ReservationOpened>
    ) {
        reservationsQueryModelRepository.createActiveReservation(
            ReservationId(eventEnvelope.entityId.value), eventEnvelope.revision, eventEnvelope.event
        )
    }

    override fun asyncAddBikes(
        eventEnvelope: EventEnvelope<ReservationId, BikesAddedToReservation>
    ) {
        println("uhhh ${eventEnvelope.event.bikeIds}, ${eventEnvelope.entityId}")

        reservationsQueryModelRepository.addBikesToReservation(
            eventEnvelope.entityId, eventEnvelope.revision, eventEnvelope.event.bikeIds
        )
    }

    override fun asyncRemoveBikes(
        eventEnvelope: EventEnvelope<ReservationId, BikesRemovedFromReservation>
    ) {
        reservationsQueryModelRepository.removeBikes(
            ReservationId(eventEnvelope.entityId.value), eventEnvelope.event.bikeIds
        )
    }

    override fun asyncArchiveReservation(reservationId: ReservationId) {
     reservationsQueryModelRepository.archiveReservation(reservationId)
    }
}