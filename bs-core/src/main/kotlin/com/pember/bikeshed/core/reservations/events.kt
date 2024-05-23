package com.pember.bikeshed.core.reservations

import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.UserId
import com.pember.eventsource.Event
import com.pember.eventsource.EventAlias
import java.time.Instant

@EventAlias("reservation:opened")
data class ReservationOpened(val customerId: UserId,
                             val expectedStartTime: Instant,
                             val expectedEndTime: Instant): Event

@EventAlias("reservation:bikes:added")
data class BikesAddedToReservation(val bikeIds: List<BikeId>): Event

@EventAlias("reservation:bikes:removed")
data class BikesRemovedFromReservation(val bikeIds: List<BikeId>): Event

@EventAlias("reservation:begun")
data class ReservationBegun(val actualStartTime: Instant): Event

@EventAlias("reservation:finished")
data class ReservationCompleted(val actualEndTime: Instant): Event

@EventAlias("reservation:cancelled")
data class ReservationCancelled(val reason: String): Event