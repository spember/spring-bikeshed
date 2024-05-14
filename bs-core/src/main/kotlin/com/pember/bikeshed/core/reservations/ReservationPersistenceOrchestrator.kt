package com.pember.bikeshed.core.reservations

import com.pember.bikeshed.core.ReservationId
import com.pember.eventsource.EntityWithEvents

interface ReservationPersistenceOrchestrator {
    fun persistNewReservation(entityWithEvents: EntityWithEvents<ReservationId, Reservation>)
}