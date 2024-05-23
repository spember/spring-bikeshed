package com.pember.bikeshed.http

import com.pember.bikeshed.auth.PrincipalAuthService
import com.pember.bikeshed.config.Constants
import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.bikes.BikeAvailabilityRepository
import com.pember.bikeshed.core.bikes.BikeManagementService
import com.pember.eventsource.EventRepository
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody


@CrossOrigin(origins = [Constants.CORS_ORIGINS])
@Controller
class BikeController(
    private val principalAuthService: PrincipalAuthService,
    private val bikeManagementService: BikeManagementService,
    private val bikeAvailabilityRepository: BikeAvailabilityRepository,
    private val eventRepository: EventRepository<String>
) {

    @ResponseBody
    @GetMapping("/bikes", consumes = ["application/json"], produces = ["application/json"])
    fun getAvailableBikes(): ResponseEntity<List<String>> {
        log.info("Hello ${principalAuthService.retrieveCurrentEmployee().name}")
        val availableBikes = bikeAvailabilityRepository.getAvailableBikes()
        return ResponseEntity.ok(availableBikes.map { it.value })
    }

    @ResponseBody
    @GetMapping("/bikes/{id}", consumes = ["application/json"], produces = ["application/json"])
    fun getBikeById(@PathVariable("id") bikeId: String): ResponseEntity<BikeDetails> {
        val foundBike = bikeManagementService.getBikeById(BikeId(bikeId))
        return if (foundBike == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(BikeDetails(foundBike.id.value, foundBike.description))
        }
    }

    @ResponseBody
    @GetMapping("/bikes/{id}/history", consumes = ["application/json"], produces = ["application/json"])
    fun getBikeHistory(@PathVariable("id") bikeId: String): ResponseEntity<List<BikeEvent>> {
        val foundBike = bikeManagementService.getBikeById(BikeId(bikeId))
        return if (foundBike == null) {
            ResponseEntity.notFound().build()
        } else {

            val history = mutableListOf<BikeEvent>()
            eventRepository.loadForId(foundBike.id).forEach {
                history.add(BikeEvent(it.event::class.simpleName!!, it.timeOccurred.toString(), it.agent))
            }
            ResponseEntity.ok(history)
        }
    }


    data class BikeDetails(val bikeId: String, val description: String)

    data class BikeEvent(val action: String, val timestamp: String, val agent: String)

    companion object {
        private val log = LoggerFactory.getLogger(BikeController::class.java)
    }
}