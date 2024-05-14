package com.pember.bikeshed.core.reservations

import com.pember.bikeshed.core.OpenNewReservation
import com.pember.bikeshed.core.ReservationId
import com.pember.bikeshed.core.UserId
import com.pember.eventsource.EntityWithEvents
import com.pember.eventsource.EventRepository
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class ReservationService(
    private val reservationPersistenceOrchestrator: ReservationPersistenceOrchestrator
) {

    fun handle(command: OpenNewReservation): ReservationId {
        // does customer have a currently open reservation? Does it matter?
        val reservation = Reservation(generateResId(command.customerId))
        val ewe = EntityWithEvents(reservation, command.employee.value)
        ewe.apply(ReservationOpened(
            command.customerId,
            command.expectedStartTime,
            command.expectedEndTime))

        reservationPersistenceOrchestrator.persistNewReservation(ewe)
        log.info("opened new reservation for customer ${command.customerId.value}")
        return ewe.entity.resId
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