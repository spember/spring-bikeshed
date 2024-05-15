package com.pember.bikeshed.core.reservations

import com.pember.bikeshed.core.AddBikesToReservation
import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.OpenNewReservation
import com.pember.bikeshed.core.ReservationId
import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.bikes.Bike
import com.pember.bikeshed.core.bikes.BikeRented
import com.pember.bikeshed.core.common.EntityStore
import com.pember.eventsource.EntityWithEvents
import com.pember.eventsource.EventRepository
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class ReservationService(
    private val entityStore: EntityStore<*>
) {

    fun handle(command: OpenNewReservation): ReservationId {
        // does customer have a currently open reservation? Does it matter?
        val reservation = Reservation(generateResId(command.customerId))
        val ewe = EntityWithEvents(reservation, command.employee.value)
        ewe.apply(ReservationOpened(
            command.customerId,
            command.expectedStartTime,
            command.expectedEndTime))

        entityStore.persist(ewe)
        log.info("opened new reservation for customer ${command.customerId.value}")
        return ewe.entity.resId
    }

    fun handle(command: AddBikesToReservation): List<BikeId>{
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

        val reservationEwe = EntityWithEvents(reservation, command.source.value)
            .apply(BikesAddedToReservation(bikes.map { it.id }))

        val bikeEwes = bikes.map { EntityWithEvents(it, command.source.value) }
            .map { it.apply(BikeRented(reservation.resId)) }

        val allEwes = listOf(reservationEwe) + bikeEwes
        entityStore.persistMultiple(allEwes)

        return bikeEwes.map { it.entity.id }
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