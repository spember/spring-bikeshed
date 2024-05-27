package com.pember.bikeshed.http.admin

import com.pember.bikeshed.config.Constants
import com.pember.bikeshed.core.reservations.ReservationQueryService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@CrossOrigin(origins = [Constants.CORS_ORIGINS])
@Controller()
@RequestMapping("/admin/reservations")
class ReservationAdminController(private val reservationQueryService: ReservationQueryService) {

    @ResponseBody
    @GetMapping("/historical", consumes = ["application/json"], produces = ["application/json"])
    fun listHistoricalReservations(): ResponseEntity<List<ReservationResponse>> {
        reservationQueryService.getHistoricalReservations().let { reservations ->
            println("Returning ${reservations}")
            return ResponseEntity.ok(reservations.map {
                ReservationResponse(it.id.value, it.customer!!.value, it.startTime.toString(), it.endTime.toString(), it.status.name)
            })
        }
    }

    data class ReservationResponse(val reservationId: String, val userId: String, val startTime: String, val endTime: String, val status: String)
}