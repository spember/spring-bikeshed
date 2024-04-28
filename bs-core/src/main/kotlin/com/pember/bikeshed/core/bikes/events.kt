package com.pember.bikeshed.core.bikes

import com.pember.bikeshed.core.UserId
import com.pember.eventsource.Event
import java.time.Instant

/*
One might observe that these events do not have things like the Bike Id -> recall that the entity id and the user id
are part of the event envelope, so we don't need to include them in the event itself.
*/


data class BikeAddedToInventory(val color: BikeColor, val fromSource: String): Event

data class BikeRented(val renter: UserId, val expectedReturn: Instant, val rentalFee: Int): Event
// rental fee assumes price per hour. In reality could have multiple schemes.

data class BikeReturned(val toSource: String): Event

data class HeldForRepairs(val reason: String): Event

data class RepairsCompleted(val costToRepair: Int): Event

data class BikeRetired(val reason: String): Event
