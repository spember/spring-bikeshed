package com.pember.bikeshed.core.bikes

import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.RegisterNewBike
import com.pember.bikeshed.core.UserId
import com.pember.eventsource.EntityWithEvents
import com.pember.eventsource.EventRepository
import org.slf4j.LoggerFactory

class BikeManagementService(
    private val eventRepository: EventRepository<String>
) {
    // if time, break this up into 'Use Cases'


    fun registerNewBike(registerNewBike: RegisterNewBike): BikeId {
        val bike = Bike(registerNewBike.bikeId)
        val ewe = EntityWithEvents(bike, registerNewBike.source.value)
        ewe.apply(BikeAddedToInventory(registerNewBike.color, registerNewBike.purchasedFrom))
        eventRepository.persist(ewe.uncommittedEvents)
        log.info("Entered new bike '${registerNewBike.bikeId}' into inventory. It has ${eventRepository.countEventsForId(registerNewBike.bikeId)} events")
        return bike.id
    }

    fun initiateRepairs(bikeId: BikeId, agent: UserId, repairDescription: String) {

        val ewe = getModifiableBike(bikeId, agent)
            .apply( HeldForRepairs(repairDescription) )
        eventRepository.persist(ewe.uncommittedEvents)
        log.info("Bike '${bikeId}' has been marked for repair")
        // todo: mark bike as unavailable ... how?
    }

    fun completeRepairs(bikeId: BikeId, agent: UserId, repairCost: Int) {
        val ewe = getModifiableBike(bikeId, agent)
            .apply( RepairsCompleted(repairCost) )
        eventRepository.persist(ewe.uncommittedEvents)
        log.info("Bike '${bikeId}' has been repaired")
    }

    fun retireBike(bikeId: BikeId, agent: UserId, reason: String) {
        val ewe = getModifiableBike(bikeId, agent)
            .apply( BikeRetired(reason) )
        eventRepository.persist(ewe.uncommittedEvents)
        log.info("Bike '${bikeId}' has been retired")
    }


    private fun getModifiableBike(bikeId: BikeId, agent: UserId): EntityWithEvents<BikeId, Bike> {
        val bike = getBikeById(bikeId) ?: throw IllegalArgumentException("Bike '${bikeId}' not found in inventory")
        println("Have a bike with id ${bike.id} at revision ${bike.revision}")
        val ewe = EntityWithEvents(bike, agent.value)
        return ewe
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