package com.pember.bikeshed.core.reservations

import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.ReservationId

interface ReservationsQueryModelRepository {

    fun getOpenReservationIds(): List<ReservationId>

    fun createActiveReservation(reservationId: ReservationId, revision: Int, event: ReservationOpened)

    fun addBikesToReservation(reservationId: ReservationId, revision: Int, bikeIds: List<BikeId>)

    fun removeBikes(reservationId: ReservationId, bikeIds: List<BikeId>)

    fun archiveReservation(reservationId: ReservationId)
}