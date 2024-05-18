package com.pember.bikeshed.sql

import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.ReservationId
import com.pember.bikeshed.core.reservations.ReservationOpened
import com.pember.bikeshed.core.reservations.ReservationsQueryModelRepository
import com.pember.bikeshed.db.jooq.tables.CurrentOpenReservations.CURRENT_OPEN_RESERVATIONS
import com.pember.bikeshed.db.jooq.tables.InactiveReservations.INACTIVE_RESERVATIONS
import com.pember.bikeshed.db.jooq.tables.ReservationBikes.RESERVATION_BIKES
import org.jooq.DSLContext
import java.time.LocalDateTime
import java.time.ZoneOffset

class JooqReservationsQueryModelRepository(private val jooq: DSLContext): ReservationsQueryModelRepository {

    override fun getOpenReservationIds(): List<ReservationId> = jooq.selectFrom(CURRENT_OPEN_RESERVATIONS)
            .fetch()
            .map { it.reservationId }
            .map { ReservationId(it) }.toList()

    override fun createActiveReservation(reservationId: ReservationId, revision: Int, event: ReservationOpened) {
        jooq.insertInto(CURRENT_OPEN_RESERVATIONS)
            .columns(
                CURRENT_OPEN_RESERVATIONS.RESERVATION_ID,
                CURRENT_OPEN_RESERVATIONS.REVISION,
                CURRENT_OPEN_RESERVATIONS.CUSTOMER_ID,
                CURRENT_OPEN_RESERVATIONS.START_TIME,
                CURRENT_OPEN_RESERVATIONS.END_TIME
            )
            .values(
                reservationId.value,
                revision,
                event.customerId.value,
                LocalDateTime.ofInstant(event.expectedStartTime, ZoneOffset.UTC),
                LocalDateTime.ofInstant(event.expectedEndTime, ZoneOffset.UTC)
            )
            .execute()
    }

    override fun addBikesToReservation(reservationId: ReservationId, revision: Int, bikeIds: List<BikeId>) {
        jooq.transaction { trx->
            val tx = trx.dsl()
            val q = tx.insertInto(RESERVATION_BIKES).columns(
                RESERVATION_BIKES.RESERVATION_ID,
                RESERVATION_BIKES.BIKE_ID,
                RESERVATION_BIKES.STATUS
            )
            bikeIds.forEach {
                q.values(
                    reservationId.value,
                    it.value,
                    "RESERVED"
                )
            }
            q.execute()
           updateReservationRevision(tx, reservationId, revision)
        }
    }


    override fun removeBikes(reservationId: ReservationId, revision: Int, bikeIds: List<BikeId>) {
        jooq.transaction { trx ->
            val tx = trx.dsl()
            tx.deleteFrom(RESERVATION_BIKES)
                .where(RESERVATION_BIKES.RESERVATION_ID.eq(reservationId.value))
                .and(RESERVATION_BIKES.BIKE_ID.`in`(bikeIds.map { it.value }))
                .execute()
            updateReservationRevision(tx, reservationId, revision)
        }

    }

    private fun updateReservationRevision(dslContext: DSLContext, reservationId: ReservationId, revision: Int) =
        dslContext
            .update(CURRENT_OPEN_RESERVATIONS)
            .set(CURRENT_OPEN_RESERVATIONS.REVISION, revision)
            .where(CURRENT_OPEN_RESERVATIONS.RESERVATION_ID.eq(reservationId.value))
            .and(CURRENT_OPEN_RESERVATIONS.REVISION.eq(revision - 1))
            .execute()

    override fun archiveReservation(reservationId: ReservationId) {
        val foundRes = jooq.selectFrom(CURRENT_OPEN_RESERVATIONS)
            .where(CURRENT_OPEN_RESERVATIONS.RESERVATION_ID.eq(reservationId.value))
            .fetchOne()
        if (foundRes == null) {
            // attempted to archive a reservation that doesn't exist in our QM
            return
        }


        jooq.transaction { config ->
            val tx = config.dsl()
            tx.insertInto(INACTIVE_RESERVATIONS)
                .columns(
                    INACTIVE_RESERVATIONS.RESERVATION_ID,
                    INACTIVE_RESERVATIONS.REVISION,
                    INACTIVE_RESERVATIONS.CUSTOMER_ID,
                    INACTIVE_RESERVATIONS.START_TIME,
                    INACTIVE_RESERVATIONS.END_TIME
                )
                .values(
                    reservationId.value,
                    foundRes.revision,
                    foundRes.customerId,
                    foundRes.startTime,
                    foundRes.endTime
                ).execute()

            tx.deleteFrom(RESERVATION_BIKES)
                .where(
                    RESERVATION_BIKES.RESERVATION_ID.eq(reservationId.value)
                )
                .execute()

            tx.deleteFrom(CURRENT_OPEN_RESERVATIONS)
                .where(CURRENT_OPEN_RESERVATIONS.RESERVATION_ID.eq(reservationId.value))
                .execute()
        }
    }


}