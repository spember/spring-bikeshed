package com.pember.bikeshed.core.reservations

import com.pember.bikeshed.core.AddBikesToReservation
import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.CompleteReservation
import com.pember.bikeshed.core.OpenNewReservation
import com.pember.bikeshed.core.ReservationId
import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.bikes.Bike
import com.pember.bikeshed.core.bikes.BikeRented
import com.pember.bikeshed.core.bikes.BikeReturned
import com.pember.bikeshed.core.common.EntityStore
import com.pember.eventsource.EntityWithEvents
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class ReservationService(
    private val entityStore: EntityStore<*>
) {

    fun process(command: OpenNewReservation): ReservationId {
        // does customer have a currently open reservation? Does it matter?
        val reservation = Reservation(generateResId(command.customerId))
        val ewe = EntityWithEvents(reservation, command.employee.value, command.occurredAt)
        ewe.apply(ReservationOpened(
            command.customerId,
            command.expectedStartTime,
            command.expectedEndTime))

        entityStore.persist(ewe)
        log.info("opened new reservation for customer ${command.customerId.value}")
        return ewe.entity.resId
    }

    fun process(command: AddBikesToReservation): List<BikeId>{
        // load bikes, load reservation
        val reservation = entityStore.loadCurrentState(Reservation(command.reservationId))
        if (!reservation.mayEditBikes()) {
            log.warn("Attempt to add bikes to a reservation that is not editable")
            return emptyList()
        }

        val bikes = entityStore
            .loadCurrentState(command.bikeIds.map { Bike(it) })
            .filter { it.isAvailableToRent() }
        // in real life we may want to figure out which bikes are unavailable and alert the user
        log.info("Marking ${bikes.size} for reservation out of ${command.bikeIds.size} requested")

        val reservationEwe = EntityWithEvents(reservation, command.agent.value, command.occurredAt)
            .apply(BikesAddedToReservation(bikes.map { it.id }))

        val bikeEwes = bikes.map { EntityWithEvents(it, command.agent.value, command.occurredAt) }
            .map { it.apply(BikeRented(reservation.resId)) }

        val allEwes = listOf(reservationEwe) + bikeEwes
        entityStore.persistMultiple(allEwes)

        return bikeEwes.map { it.entity.id }
    }

    fun process(command: CompleteReservation) {
        val reservation = entityStore.loadCurrentState(Reservation(command.reservationId))
        if (!reservation.mayEditBikes()) {
            log.warn("Attempt to add bikes to a reservation that is not editable")
            return
        }
        val reservationEwe = EntityWithEvents(reservation, command.agent.value, command.occurredAt)
            .apply(ReservationCompleted(command.occurredAt))

        log.info("Reservation has ${reservation.getClaimedBikes().size} bikes rented")
        val bikeEwes = entityStore.loadCurrentState(reservation.getClaimedBikes().map { Bike(it) })
            .map { EntityWithEvents(it, command.agent.value, command.occurredAt) }

        bikeEwes.forEach { it.apply(BikeReturned("safely")) }

        entityStore.persistMultiple(bikeEwes + reservationEwe)

        log.info("Completed reservation ${command.reservationId.value}")

    }


    private fun generateResId(customerId: UserId): ReservationId {
        // generate a reservation id based on the current time and the customer Id
        val local = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
        return ReservationId("${customerId.value}-${local.year}-${local.month}-${local.nano}")

    }

    companion object {
        private val log = LoggerFactory.getLogger(ReservationService::class.java)
    }
}