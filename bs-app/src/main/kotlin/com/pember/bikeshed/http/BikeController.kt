package com.pember.bikeshed.http

import com.pember.bikeshed.config.Constants
import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.bikes.BikeManagementService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody


@CrossOrigin(origins = [Constants.CORS_ORIGINS])
@Controller
class BikeController(private val bikeManagementService: BikeManagementService) {

    @ResponseBody
    @GetMapping("/bike/{id}", consumes = ["application/json"], produces = ["application/json"])
    fun getBikeById(@PathVariable("id") bikeId: String): ResponseEntity<BikeDetails> {
        val foundBike = bikeManagementService.getBikeById(BikeId(bikeId))
        log.info("looking for bike by $bikeId returned ${foundBike}")
        return if (foundBike == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(BikeDetails(foundBike.id, foundBike.description))
        }

    }


    data class BikeDetails(val bikeId: BikeId, val description: String)

    companion object {
        private val log = LoggerFactory.getLogger(BikeController::class.java)
    }
}