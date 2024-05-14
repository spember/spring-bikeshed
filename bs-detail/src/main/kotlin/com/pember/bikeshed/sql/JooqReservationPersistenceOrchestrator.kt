package com.pember.bikeshed.sql

import com.pember.bikeshed.core.ReservationId
import com.pember.bikeshed.core.reservations.Reservation
import com.pember.bikeshed.core.reservations.ReservationPersistenceOrchestrator
import com.pember.eventsource.EntityWithEvents
import org.jooq.DSLContext

class JooqReservationPersistenceOrchestrator(
    private val dslContext: DSLContext,
    private val jooqEventRepository: JooqEventRepository,
): ReservationPersistenceOrchestrator {
    override fun persistNewReservation(entityWithEvents: EntityWithEvents<ReservationId, Reservation>) {
        dslContext.transaction { trx->
            jooqEventRepository.withTx(trx).persist(entityWithEvents.uncommittedEvents)
            // update projection
        }
    }


    // projections register in event observer. each event is passed to the projection updater
    // along with the client/tx object
    // add BikeToReservation -> available Bike List





}