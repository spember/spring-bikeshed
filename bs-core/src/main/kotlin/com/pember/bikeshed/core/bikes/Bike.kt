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

    override fun reactToIncomingEvent(eventEnvelope: EventEnvelope<BikeId, out Event>): Boolean {
        Foo()
        return when (val event = eventEnvelope.event) {
            is BikeAddedToInventory -> {
                description = "A ${event.color} bike purchased from ${event.fromSource}"
                bikeColor = event.color
                origin = event.fromSource
                true
            }
            else -> false
        }
    }
}