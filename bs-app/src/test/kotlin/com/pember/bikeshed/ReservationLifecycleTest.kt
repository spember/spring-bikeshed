package com.pember.bikeshed

import com.pember.bikeshed.core.OpenNewReservation
import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.reservations.Reservation
import com.pember.bikeshed.core.reservations.ReservationService
import com.pember.bikeshed.support.BaseIntegrationTest
import com.pember.eventsource.EventRepository
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.time.temporal.ChronoUnit

class ReservationLifecycleTest: BaseIntegrationTest() {

    @Autowired
    lateinit var reservationService: ReservationService

    @Autowired
    lateinit var eventRepository: EventRepository<String>

    @Test
    fun `Opening a Reservation should reflect the user and claim the Bike`() {

        val employee = UserId("stu")
        val customer = UserId("Bob Smith")

        val resId = reservationService.handle(
            OpenNewReservation(
                employee,
                customer,
                Instant.now().plusSeconds(5),
                Instant.now().plus(60, ChronoUnit.MINUTES),
                listOf()
            ))



        val res = Reservation(resId)
        eventRepository.loadForId(resId).forEach { event ->
            res.apply(event)
        }

        assertEquals(res.status, Reservation.Status.PENDING)
        assertEquals(res.customer, customer)
        assertEquals(true, res.getClaimedBikes().isEmpty())
    }
}