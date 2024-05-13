package com.pember.bikeshed.core.bikes

import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.Foo
import com.pember.eventsource.DomainEntity
import com.pember.eventsource.Event
import com.pember.eventsource.EventEnvelope

class Bike(val id: BikeId): DomainEntity<BikeId>(id) {

    var description: String = ""
        private set

    var bikeColor: BikeColor? = null
        private set

    var origin: String = ""
        private set

    var available: Boolean = true
        private set

    var timesRepaired: Int = 0
        private set

    override fun reactToIncomingEvent(eventEnvelope: EventEnvelope<BikeId, out Event>): Boolean {
        return when (val event = eventEnvelope.event) {
            /*
            An event is essentially a 'fancy setter' for our Entities.

            Our Entities are mutable, our Events are not.
             */
            is BikeAddedToInventory -> {
                description = "A ${event.color} bike purchased from ${event.fromSource}"
                bikeColor = event.color
                origin = event.fromSource
                true
            }

            is HeldForRepairs -> {
                available = false
                true
            }
            is RepairsCompleted -> {
                available = true
                timesRepaired++
                true
            }
            is BikeRetired -> {
                available = false
                active = false
                true
            }
            else -> false
        }
    }
}