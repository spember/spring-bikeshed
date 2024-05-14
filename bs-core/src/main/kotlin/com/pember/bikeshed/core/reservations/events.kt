package com.pember.bikeshed.core.reservations

import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.UserId
import com.pember.eventsource.Event
import java.time.Instant

data class ReservationOpened(val customerId: UserId,
                             val expectedStartTime: Instant,
                             val expectedEndTime: Instant): Event

data class BikesAddedToReservation(val bikeIds: List<BikeId>): Event

data class BikesRemovedFromReservation(val bikeIds: List<BikeId>): Event

data class ReservationBegun(val actualStartTime: Instant): Event

data class ReservationCompleted(val actualEndTime: Instant): Event

data class ReservationCancelled(val reason: String): Event