package com.pember.bikeshed.core.reservations

import com.pember.bikeshed.core.common.EntityStore

class ReservationQueryService(
    private val reservationsQueryModelRepository: ReservationsQueryModelRepository,
    private val entityStore: EntityStore<*>
) {

    fun getOpenReservations(): List<Reservation> {
        return listOf()
    }

    fun getHistoricalReservations(): List<Reservation> {
        return listOf()
    }
}