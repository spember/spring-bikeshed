package com.pember.bikeshed.http.admin

import com.pember.bikeshed.auth.PrincipalAuthService
import com.pember.bikeshed.config.Constants
import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.bikes.BikeManagementService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

/**
 * Intended to contain endpoints for an employee to hit when managing bikes -> marking for repair,
 * handling a new bike, etc
 */
@CrossOrigin(origins = [Constants.CORS_ORIGINS])
@Controller()
@RequestMapping("/admin/bikes")
class BikeAdminController(
    private val authService: PrincipalAuthService,
    private val bikeManagementService: BikeManagementService
) {

    data class BikeRepairRequest(val bikeId: String, val reason: String)

    @ResponseBody
    @PostMapping("/repair", consumes = ["application/json"], produces = ["application/json"])
    fun markBikeForRepair(@RequestBody payload: BikeRepairRequest): ResponseEntity<String> {
        val currentEmployee = authService.retrieveCurrentEmployee()
        log.info("${currentEmployee.name} is attempting to mark bike ${payload.bikeId} for repair")
        try {
            bikeManagementService.initiateRepairs(BikeId(payload.bikeId), currentEmployee.id, payload.reason)
            return ResponseEntity.ok("Bike ${payload.bikeId} marked for repair")
        } catch(e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body("Bike ${payload.bikeId} not found")
        }
    }


    data class CompleteRepairsRequest(val bikeId: String, val repairCost: Int)

    @ResponseBody
    @PostMapping("/repairs-complete", consumes = ["application/json"], produces = ["application/json"])
    fun markBikeAsRepaired(@RequestBody payload: CompleteRepairsRequest): ResponseEntity<String> {
        val currentEmployee = authService.retrieveCurrentEmployee()
        log.info("${currentEmployee.name} is marking bike ${payload.bikeId} that it's fixed, and cost ${payload.repairCost}")
        try {
            bikeManagementService.completeRepairs(BikeId(payload.bikeId), currentEmployee.id, payload.repairCost)
            return ResponseEntity.ok("Bike ${payload.bikeId} should be back in action!")
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body("Bike ${payload.bikeId} not found")
        }

    }

    companion object {
        private val log = LoggerFactory.getLogger(BikeAdminController::class.java)
    }
}