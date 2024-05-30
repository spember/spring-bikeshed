package com.pember.bikeshed.core.reservations

import com.pember.bikeshed.core.ReservationId
import com.pember.bikeshed.core.common.EntityStore

class ReservationQueryService(
    private val reservationsQueryModelRepository: ReservationsQueryModelRepository,
    private val entityStore: EntityStore<*>
) {

    fun getOpenReservations(): List<Reservation>  =
        entityStore.loadCurrentState(
            reservationsQueryModelRepository.getOpenReservationIds().map {Reservation(it)}
        )

    fun getHistoricalReservations(): List<Reservation>  =
        entityStore.loadCurrentState(
            reservationsQueryModelRepository.getPastReservationIds().map {Reservation(it)}
        )
}