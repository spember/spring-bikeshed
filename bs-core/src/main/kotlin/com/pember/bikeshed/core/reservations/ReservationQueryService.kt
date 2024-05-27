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
        // todo: make this more efficient and more detailed
        return reservationsQueryModelRepository.getPastReservationIds().mapNotNull { reservationId ->
            entityStore.loadCurrentState(Reservation(reservationId))
        }
    }
}