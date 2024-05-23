package com.pember.bikeshed

import com.pember.bikeshed.core.AddBikesToReservation
import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.CompleteReservation
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

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

        var latch = CountDownLatch(1)

        entityStore.onAsyncComplete {
            latch.countDown()
        }

        val resId = reservationService.process(
            OpenNewReservation(
                employee,
                customer,
                Instant.now().plusSeconds(5),
                Instant.now().plus(60, ChronoUnit.MINUTES)
            ))


        reservationService.process(AddBikesToReservation(customer, resId, bikeIds))


        val res = entityStore.loadCurrentState(Reservation(resId))


        assertEquals(res.status, Reservation.Status.PENDING)
        assertEquals(res.customer, customer)
        assertEquals(2, res.getClaimedBikes().size)

        latch.await(5, TimeUnit.SECONDS)
        val openResIds = reservationsQueryModelRepository.getOpenReservationIds()
        println("Have open reservations of ${openResIds}")
        assertEquals(1, openResIds.size)

        val bikes = entityStore.loadCurrentState(listOf(Bike(bikeIds[0]), Bike(bikeIds[1])))
        bikes.forEach {
            Assertions.assertFalse(it.available)
        }
        latch = CountDownLatch(1)

        reservationService.process(CompleteReservation(customer, resId))

        latch.await(5, TimeUnit.SECONDS)
    }

    @Test
    fun `Completing a reservation should restore bikes` () {
        // two bursts of events go through the async process
        var latch = CountDownLatch(2)

        entityStore.onAsyncComplete {
            latch.countDown()

        }

        val resId = reservationService.process(
            OpenNewReservation(
                employee,
                customer,
                Instant.now().plusSeconds(5),
                Instant.now().plus(60, ChronoUnit.MINUTES),
                Instant.now().minus(1, ChronoUnit.DAYS)
            ))

        reservationService.process(AddBikesToReservation(customer, resId, bikeIds))

        latch.await(5, TimeUnit.SECONDS)
        assertEquals(1, reservationsQueryModelRepository.getOpenReservationIds().size)

        val bikes = entityStore.loadCurrentState(listOf(Bike(bikeIds[0]), Bike(bikeIds[1])))
        bikes.forEach {
            Assertions.assertFalse(it.available)
        }
        // one for the res, one each for bike
        latch = CountDownLatch(1)

        reservationService.process(CompleteReservation(customer, resId))

        latch.await(5, TimeUnit.SECONDS)
        assertEquals(0, reservationsQueryModelRepository.getOpenReservationIds().size)
        assertEquals(resId, reservationsQueryModelRepository.getPastReservationIds().first())

        val bikesAfter = entityStore.loadCurrentState(listOf(Bike(bikeIds[0]), Bike(bikeIds[1])))
        bikesAfter.forEach {
            Assertions.assertTrue(it.available)
        }
    }


    @BeforeEach
    fun beforeTest() {
        if (bikeManagementService.getBikeById(bikeIds.first()) == null) {
            log.info("Inserting test data for integration test")
            bikeManagementService.process(RegisterNewBike(
                employee,
                bikeIds[0],
                BikeColor.BLUE,
                "The Bike Emporium"
            ))

            bikeManagementService.process(RegisterNewBike(
                employee,
                bikeIds[1],
                BikeColor.RED,
                "The Bike Emporium"
            ))
        }
    }

    companion object {

        private val employee = UserId("stu")
        private val customer = UserId("Bob Smith")

        private val bikeIds = listOf(
            BikeId("bike-one"),
            BikeId("bike-two")
        )

        private val log = LoggerFactory.getLogger(ReservationLifecycleTest::class.java)
    }

}