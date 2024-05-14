package com.pember.bikeshed.core.reservations

import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.ReservationId
import com.pember.bikeshed.core.UserId
import com.pember.eventsource.DomainEntity
import com.pember.eventsource.Event
import com.pember.eventsource.EventEnvelope
import com.pember.eventsource.errors.UnknownEventException
import java.time.Instant

/**
 * A "reservation" for one or more bikes, made by a non-employee user. Has a duration and a cost for that duration.
 *
 * Bikes can be claimed before the reservation starts -> e.g. the customer calls ahead to reserve some bikes, but then
 * the reservation starts once they leave the building
 */
class Reservation(val resId: ReservationId): DomainEntity<ReservationId>(resId) {

    var customer: UserId? = null
        private set

    var status = Status.PENDING
        private set

    var scheduledStartTime: Instant? = null
        private set

    var startTime: Instant? = null
        private set

    var expectedEndTime: Instant? = null
        private set

    var endTime: Instant? = null
        private set


    private val bikesRented = mutableListOf<BikeId>()

    fun getClaimedBikes(): List<BikeId> = bikesRented.toList()

    override fun receiveEvent(eventEnvelope: EventEnvelope<ReservationId, out Event>) {
        when(val event = eventEnvelope.event) {
            is ReservationOpened -> handle(event)
            else -> throw UnknownEventException(event)
        }
    }

    private fun handle(event: ReservationOpened) {
        customer = event.customerId
        scheduledStartTime = event.expectedStartTime
        expectedEndTime = event.expectedEndTime
        status = Status.PENDING
    }


    enum class Status {
        PENDING,
        ACTIVE,
        CANCELLED,
        COMPLETED
    }
}