package com.pember.bikeshed.lifecycle

import com.pember.bikeshed.config.Constants
import com.pember.bikeshed.core.AddBikesToReservation
import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.CompleteReservation
import com.pember.bikeshed.core.OpenNewReservation
import com.pember.bikeshed.core.RegisterNewBike
import com.pember.bikeshed.core.RegisterNewUser
import com.pember.bikeshed.core.bikes.BikeColor
import com.pember.bikeshed.core.bikes.BikeManagementService
import com.pember.bikeshed.core.reservations.ReservationService
import com.pember.bikeshed.core.users.UserRegistrationService
import com.pember.bikeshed.db.jooq.tables.EventJournal
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit

@Component
class SeedHistory(
    @Value("\${spring.bikeshed.seed}") private val shouldSeed: Boolean,
    private val dslContext: DSLContext,
    private val userRegistrationService: UserRegistrationService,
    private val bikeManagementService: BikeManagementService,
    private val reservationService: ReservationService
): ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        if (shouldSeed) {
            val eventCount = dslContext.selectFrom(EventJournal.EVENT_JOURNAL).count()
            log.info("Currently have ${eventCount} events in the journal")
            if (eventCount > 1) {
                log.info("Skipping seeding as we have ${eventCount} events already...")
            } else {
                log.info("Seeding Bikeshed history")
                seedBikeshedHistory()
            }
        } else {
            log.info("Skipping seeding due to configuration")
        }
    }


    /**
     * For demonstration purposes, seed a history into our app so we have something useful to play with
     *
     */
    private fun seedBikeshedHistory() {
        log.info("First, register employees and customers")
        /*
        3 months ago, the system is created. The boss and an employee are registered.
        Multiple bikes are registered.
         */
        val bossId = userRegistrationService.process(
            RegisterNewUser(Constants.SYSTEM,"Boss Sam", "sam@bikeshed.com", true,
                Instant.now().minus(90, ChronoUnit.DAYS))
        )

        val e1Id = userRegistrationService.process(RegisterNewUser(bossId, "Disco Stu", "stu@bikeshed.com", true,
            Instant.now().minus(89, ChronoUnit.DAYS))
        )

        val bikeData = listOf(
            BikeSeed("b123-blue", BikeColor.BLUE),
            BikeSeed("b124-red", BikeColor.RED),
            BikeSeed("t24-green", BikeColor.GREEN),
            BikeSeed("t24-red", BikeColor.RED, "Bob's Bikes"),
            BikeSeed("t100", BikeColor.BLUE),
        )

        bikeData.forEach {
            bikeManagementService.process(
                RegisterNewBike(e1Id, BikeId(it.id), it.color, it.purchasedFrom, Instant.now().minus(87, ChronoUnit.DAYS))
            )
        }

        /*
        2 months ago, a customer is registered
         */
        val fiveMonths = Instant.now().minus(60, ChronoUnit.DAYS)

        val customerId = userRegistrationService.process(
            RegisterNewUser(bossId, "Bob Robertson", "bigbob@gmail.com", false, fiveMonths)
        )

        // customer rents many of our bikes

        val r1 = reservationService.process(
            OpenNewReservation(
                e1Id,
                customerId,
                fiveMonths.plus(5, ChronoUnit.DAYS),
                fiveMonths.plus(5, ChronoUnit.DAYS).plus(4, ChronoUnit.HOURS),
                fiveMonths.plus(1, ChronoUnit.DAYS)
            )
        )

        reservationService.process(AddBikesToReservation(e1Id, r1, listOf(BikeId("b123-blue"),
            BikeId("b124-red"),BikeId("t24-green")), fiveMonths.plus(2, ChronoUnit.DAYS)))
        reservationService.process(AddBikesToReservation(e1Id, r1, listOf(BikeId("t100")), fiveMonths.plus(2, ChronoUnit.DAYS).plusSeconds(30)))

        Thread.sleep(1000)
        // customer returned it an hour later than expected
        reservationService.process(CompleteReservation(customerId, r1, fiveMonths.plus(5, ChronoUnit.DAYS).plus(5, ChronoUnit.HOURS)))

    }

    companion object {
        private val log = LoggerFactory.getLogger(SeedHistory::class.java)

    }

    private data class BikeSeed(val id: String, val color: BikeColor, val purchasedFrom: String="The Bike Emporium")

}