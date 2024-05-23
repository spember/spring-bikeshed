package com.pember.bikeshed.core.bikes

import com.pember.bikeshed.core.ReservationId
import com.pember.bikeshed.core.UserId
import com.pember.eventsource.Event
import com.pember.eventsource.EventAlias
import java.time.Instant

/*
One might observe that these events do not have things like the Bike Id -> recall that the entity id and the user id
are part of the event envelope, so we don't need to include them in the event itself.
*/

/*
    Events for a bicycle in a Bike Rental Store system
 */

@EventAlias("bike:added")
data class BikeAddedToInventory(val color: BikeColor,
                                val fromSource: String): Event

@EventAlias("bike:rented")
data class BikeRented(val reservation: ReservationId): Event

// uh-oh we realized that our original bike rented event wasn't good enough, but it had been running in prod for years
// now! We can't change the original event, but we can create a new version of it.
@EventAlias("bike:rented:v2")
data class BikeRentedV2(val reservation: ReservationId,
                        val discount: Int): Event

@EventAlias("bike:returned")
data class BikeReturned(val toSource: String): Event

@EventAlias("bike:in-for-repairs")
data class HeldForRepairs(val reason: String): Event

@EventAlias("bike:repairs:completed")
data class RepairsCompleted(val costToRepair: Int): Event

@EventAlias("bike:retired")
data class BikeRetired(val reason: String): Event



// rental fee assumes price per hour. In reality could have multiple schemes.