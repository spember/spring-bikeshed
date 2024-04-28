package com.pember.bikeshed.core.bikes

import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.RegisterNewBike
import com.pember.eventsource.EntityWithEvents
import com.pember.eventsource.EventRepository
import org.slf4j.LoggerFactory

class BikeManagementService(
    private val eventRepository: EventRepository<String>
) {
    // if time, break this up into 'Use Cases'


    fun registerNewBike(registerNewBike: RegisterNewBike) {
        val bike = Bike(registerNewBike.bikeId)
        val ewe = EntityWithEvents(bike, registerNewBike.source.value)
        ewe.apply(BikeAddedToInventory(registerNewBike.color, registerNewBike.purchasedFrom))
        eventRepository.persist(ewe.uncommittedEvents)
        log.info("Entered new bike '${registerNewBike.bikeId}' into inventory. It has ${eventRepository.countEventsForId(registerNewBike.bikeId)} events")
    }

    fun getBikeById(bikeId: BikeId): Bike? {
        val bike = Bike(bikeId)
        eventRepository.loadForId(bikeId).forEach { envelope ->
            bike.apply(envelope)
        }

        return if (bike.revision == 0) {
            null
        } else {
            bike
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(BikeManagementService::class.java)
    }
}