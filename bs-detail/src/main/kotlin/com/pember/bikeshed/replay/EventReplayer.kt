package com.pember.bikeshed.replay

import com.pember.bikeshed.core.projections.ProjectionOrchestrator
import com.pember.bikeshed.sql.JooqProjectionOrchestrator
import com.pember.eventsource.EntityId
import com.pember.eventsource.EventRepository
import org.jooq.DSLContext

/**
 * An example of a tool used for 'replaying' events. In this particular case we use threads to
 * replay events, but in reality we'd likely want a message broker or Kafka stream.
 */
class EventReplayer(
    private val dslContext: DSLContext,
    private val eventRepository: EventRepository<String>,
    private val projectionOrchestrator: JooqProjectionOrchestrator
) {

    fun replayEvents() {
        // under the hood we use Jooq's fetchStream, and kotlin's use auto-closes it
        eventRepository.streamAllEvents().use { stream ->
            stream.forEach { eventEnvelope ->
                dslContext.transaction { trx ->
                    projectionOrchestrator.receiveEventForConstraints(trx, eventEnvelope)
                }
                projectionOrchestrator.receiveEventEventually(eventEnvelope)
            }
        }
    }
}