package com.pember.bikeshed.http

import com.pember.bikeshed.auth.PrincipalAuthService
import com.pember.bikeshed.config.Constants
import com.pember.bikeshed.core.AddBikesToReservation
import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.CompleteReservation
import com.pember.bikeshed.core.OpenNewReservation
import com.pember.bikeshed.core.ReservationId
import com.pember.bikeshed.core.common.EntityStore
import com.pember.bikeshed.core.reservations.Reservation
import com.pember.bikeshed.core.reservations.ReservationService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import java.time.Instant
import java.time.temporal.ChronoUnit

@CrossOrigin(origins = [Constants.CORS_ORIGINS])
@Controller()
@RequestMapping("/reservations")
class ReservationController(
    private val authService: PrincipalAuthService,
    private val reservationService: ReservationService,
    private val entityStore: EntityStore<*>
) {

    data class ReservationRequest(val durationHours: Int)
    data class BikeRequest(val bikeIds: List<String>)

    data class ReservationResponse(
        val reservationId: String,
        val userId: String,
        val startTime: String,
        val endTime: String,
        val status: String,
        val bikeCount: Int
    )

    @PostMapping("/", consumes = ["application/json"], produces = ["application/json"])
    fun openReservation(@RequestBody payload: ReservationRequest): ResponseEntity<String> {
        /*
        Because I didn't want to wrangle with converting time, all reservations start now() plus some time
         */
        try {
            val id = reservationService.process(
                OpenNewReservation(
                    authService.retrieveCurrentEmployee().id,
                    authService.retrieveCurrentCustomer().id,
                    Instant.now(),
                    Instant.now().plus(payload.durationHours.toLong(), ChronoUnit.HOURS)
                )
            )
            return ResponseEntity.ok(id.value)
        } catch (e: IllegalArgumentException) {
            return ResponseEntity.badRequest().body("Failed to open reservation")
        }
    }

    @GetMapping("/{resId}", consumes = ["application/json"], produces = ["application/json"])
    fun viewReservation(@PathVariable("resId") resId: String): ResponseEntity<ReservationResponse> {
        val reservation = entityStore.loadCurrentState(Reservation(ReservationId(resId)))

        return if (reservation.active.not()) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(
                ReservationResponse(
                    reservation.id.value,
                    reservation.customer!!.value,
                    reservation.startTime.toString(),
                    reservation.endTime.toString(),
                    reservation.status.name,
                    reservation.getClaimedBikes().size
                )
            )
        }
    }

    @PostMapping("/{resId}", consumes = ["application/json"], produces = ["application/json"])
    fun addBikeToReservation(@PathVariable("resId") resId: String, @RequestBody payload: BikeRequest) {
        val reservation = entityStore.loadCurrentState(Reservation(ReservationId(resId)))
        if (reservation.active.not()) {
            throw IllegalArgumentException("Reservation is not active")
        }
        reservationService.process(
            AddBikesToReservation(
                authService.retrieveCurrentEmployee().id,
                reservation.id,
                payload.bikeIds.map {BikeId(it)}
            )
        )
    }


    @PostMapping("/{resId}/complete", consumes = ["application/json"], produces = ["application/json"])
    fun completeReservation(@PathVariable("resId") resId: String) {
        val reservation = entityStore.loadCurrentState(Reservation(ReservationId(resId)))
        if (reservation.active.not()) {
            throw IllegalArgumentException("Reservation is not active")
        }
        reservationService.process(
            CompleteReservation(
                authService.retrieveCurrentEmployee().id,
                reservation.id
            )
        )

    }
}