package com.pember.bikeshed.core.bikes

import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.RegisterNewBike
import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.common.EntityStore
import com.pember.eventsource.EntityWithEvents
import org.slf4j.LoggerFactory

class BikeManagementService(
    private val entityStore: EntityStore<*>,
    private val bikeAvailabilityRepository: BikeAvailabilityRepository
) {
    // if time, break this up into 'Use Cases'

    fun getAvailableBikes(): List<BikeId> {
        return bikeAvailabilityRepository.getAvailableBikes()
    }

    fun registerNewBike(registerNewBike: RegisterNewBike): BikeId {
        val bike = Bike(registerNewBike.bikeId)
        val ewe = EntityWithEvents(bike, registerNewBike.source.value)
        ewe.apply(BikeAddedToInventory(registerNewBike.color, registerNewBike.purchasedFrom))
        entityStore.persist(ewe)
        log.info("Bike '${registerNewBike.bikeId}' has been added to inventory")
        return bike.id
    }

    fun initiateRepairs(bikeId: BikeId, agent: UserId, repairDescription: String) {

        val ewe = getModifiableBike(bikeId, agent)
            .apply( HeldForRepairs(repairDescription) )
        entityStore.persist(ewe)
        log.info("Bike '${bikeId}' has been marked for repair")
    }

    fun completeRepairs(bikeId: BikeId, agent: UserId, repairCost: Int) {
        val ewe = getModifiableBike(bikeId, agent)
            .apply( RepairsCompleted(repairCost) )
        entityStore.persist(ewe)
        log.info("Bike '${bikeId}' has been repaired")
    }

    fun retireBike(bikeId: BikeId, agent: UserId, reason: String) {
        val ewe = getModifiableBike(bikeId, agent)
            .apply( BikeRetired(reason) )
        entityStore.persist(ewe)
        log.info("Bike '${bikeId}' has been retired")
    }


    private fun getModifiableBike(bikeId: BikeId, agent: UserId): EntityWithEvents<BikeId, Bike> {
        val bike = getBikeById(bikeId) ?: throw IllegalArgumentException("Bike '${bikeId}' not found in inventory")
        val ewe = EntityWithEvents(bike, agent.value)
        return ewe
    }


    fun getBikeById(bikeId: BikeId): Bike? {
        val bike = entityStore.loadCurrentState(Bike(bikeId))
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