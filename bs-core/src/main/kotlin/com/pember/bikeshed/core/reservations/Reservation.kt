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
        /*
        In other entities we might do large blocks for each of these event handlers, but in this case we're
        choosing to use "fancy private setters" to make this block more succinct. This is a stylistic choice.
         */
        when(val event = eventEnvelope.event) {
            is ReservationOpened -> handle(event)
            is BikesAddedToReservation -> handle(event)
            is BikesRemovedFromReservation -> handle(event)
            is ReservationBegun -> handle(event)
            is ReservationCompleted -> handle(event)
            // nothing wrong with all handing the full envelope to get access to time
            is ReservationCancelled -> handle(event, eventEnvelope)

            else -> throw UnknownEventException(event)
        }
    }

    private fun handle(event: ReservationOpened) {
        customer = event.customerId
        scheduledStartTime = event.expectedStartTime
        expectedEndTime = event.expectedEndTime
        status = Status.PENDING
    }

    private fun handle(event: BikesAddedToReservation){
        this.bikesRented.addAll(event.bikeIds)
    }

    private fun handle(event: BikesRemovedFromReservation){
        this.bikesRented.removeAll(event.bikeIds)
    }

    private fun handle(event: ReservationBegun) {
        startTime = event.actualStartTime
        status = Status.ACTIVE
    }

    private fun handle(event: ReservationCompleted) {
        endTime = event.actualEndTime
        status = Status.COMPLETED
    }

    private fun handle(event: ReservationCancelled, eventEnvelope: EventEnvelope<ReservationId, out Event>) {
        status = Status.CANCELLED
        endTime = eventEnvelope.timeOccurred
    }

    enum class Status {
        PENDING,
        ACTIVE,
        CANCELLED,
        COMPLETED
    }
}