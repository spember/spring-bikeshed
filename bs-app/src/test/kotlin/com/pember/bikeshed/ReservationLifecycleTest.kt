package com.pember.bikeshed

import com.pember.bikeshed.core.AddBikesToReservation
import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.OpenNewReservation
import com.pember.bikeshed.core.RegisterNewBike
import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.bikes.Bike
import com.pember.bikeshed.core.bikes.BikeColor
import com.pember.bikeshed.core.bikes.BikeManagementService
import com.pember.bikeshed.core.common.EntityStore
import com.pember.bikeshed.core.reservations.Reservation
import com.pember.bikeshed.core.reservations.ReservationService
import com.pember.bikeshed.core.reservations.ReservationsQueryModelRepository
import com.pember.bikeshed.support.BaseIntegrationTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.CountDownLatch

class ReservationLifecycleTest: BaseIntegrationTest() {

    @Autowired
    lateinit var reservationService: ReservationService

    @Autowired
    lateinit var entityStore: EntityStore<*>

    @Autowired
    lateinit var bikeManagementService: BikeManagementService

    @Autowired
    lateinit var reservationsQueryModelRepository: ReservationsQueryModelRepository

    @Test
    fun `Opening a Reservation should reflect the user and claim the Bike`() {

        val employee = UserId("stu")
        val customer = UserId("Bob Smith")
        val latch = CountDownLatch(1)
        entityStore.onAsyncComplete {
            println("*** async!")
            latch.countDown()
        }

        // create some bikes
        val b1: BikeId = bikeManagementService.registerNewBike(RegisterNewBike(
            employee,
            BikeId("bike-one"),
            BikeColor.BLUE,
            "The Bike Emporium"
        ))

        val b2 = bikeManagementService.registerNewBike(RegisterNewBike(
            employee,
            BikeId("bike-two"),
            BikeColor.RED,
            "The Bike Emporium"
        ))


        val resId = reservationService.handle(
            OpenNewReservation(
                employee,
                customer,
                Instant.now().plusSeconds(5),
                Instant.now().plus(60, ChronoUnit.MINUTES),
                listOf()
            ))


        reservationService.handle(AddBikesToReservation(customer, resId, listOf(b1, b2)))


        val res = entityStore.loadCurrentState(Reservation(resId))


        assertEquals(res.status, Reservation.Status.PENDING)
        assertEquals(res.customer, customer)
        assertEquals(2, res.getClaimedBikes().size)

        latch.await(5, java.util.concurrent.TimeUnit.SECONDS)
        assertEquals(1, reservationsQueryModelRepository.getOpenReservationIds().size)

        val bikes = entityStore.loadCurrentState(listOf(Bike(b1), Bike(b2)))
        bikes.forEach {
            Assertions.assertFalse(it.available)
        }
    }
}