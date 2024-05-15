package com.pember.bikeshed.sql

import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.projections.ProjectionOrchestrator
import com.pember.bikeshed.core.users.RoleChanged
import com.pember.bikeshed.core.users.UserCreated
import com.pember.eventsource.EventEnvelope
import org.jooq.Configuration

class JooqProjectionOrchestrator(): ProjectionOrchestrator<Configuration>() {

    override fun updateUserLookup(
        transactionalClient: Configuration,
        eventEnvelope: EventEnvelope<UserId, UserCreated>
    ) {
        JooqUserConstraintsRepository(transactionalClient.dsl()).updateUserConstraints(
            eventEnvelope.entityId, eventEnvelope.event.email, false)
    }

    override fun updateUserRole(transactionalClient: Configuration, eventEnvelope: EventEnvelope<UserId, RoleChanged>) {
        JooqUserConstraintsRepository(transactionalClient.dsl())
            .updateUserRole(eventEnvelope.entityId, eventEnvelope.event.employee)
    }

    override fun makeBikeAvailable(transactionalClient: Configuration, bikeId: BikeId, revision: Int) {
        JooqBikeAvailabilityRepository(transactionalClient.dsl()).addAvailableBike(bikeId, revision)
    }

    override fun makeBikeUnavailable(transactionalClient: Configuration, bikeId: BikeId, revision: Int) {
        JooqBikeAvailabilityRepository(transactionalClient.dsl()).removeAvailableBike(bikeId, revision)
    }


}