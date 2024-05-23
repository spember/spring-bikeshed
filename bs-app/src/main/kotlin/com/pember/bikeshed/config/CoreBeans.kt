package com.pember.bikeshed.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.pember.bikeshed.core.common.EntityStore
import com.pember.bikeshed.core.projections.ProjectionOrchestrator
import com.pember.bikeshed.core.reservations.ReservationsQueryModelRepository
import com.pember.bikeshed.replay.EventReplayer
import com.pember.bikeshed.sql.JooqEntityStore
import com.pember.bikeshed.sql.JooqEventRepository
import com.pember.bikeshed.sql.JooqProjectionOrchestrator
import com.pember.eventsource.EventRegistry
import com.pember.eventsource.EventRepository
import org.jooq.DSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CoreBeans {

    @Bean
    fun provideProjectionOrchestrator(reservationsQueryModelRepository: ReservationsQueryModelRepository): ProjectionOrchestrator<*> {
        return JooqProjectionOrchestrator(reservationsQueryModelRepository)
    }

    @Bean
    fun provideEntityStore(
        dslContext: DSLContext,
        eventRepository: EventRepository<String>,
        projectionOrchestrator: ProjectionOrchestrator<*>
    ): EntityStore<*> {
        return JooqEntityStore(
            dslContext,
            eventRepository as JooqEventRepository,
            projectionOrchestrator as JooqProjectionOrchestrator
        )
    }

    @Bean
    fun provideEventRepository(
        jooq: DSLContext,
        objectMapper: ObjectMapper,
        eventRegistry: EventRegistry
    ): EventRepository<String> {
        return JooqEventRepository(jooq, objectMapper, eventRegistry)
    }

    @Bean
    fun provideEventRegistry(): EventRegistry {
        val registry = EventRegistry()
        registry.scan("com.pember.bikeshed.core")
        return registry
    }

    @Bean
    fun provideEventReplayer(
        dslContext: DSLContext,
        eventRepository: EventRepository<String>,
        projectionOrchestrator: ProjectionOrchestrator<*>
    ): EventReplayer {
        return EventReplayer(
            dslContext,
            eventRepository,
            projectionOrchestrator as JooqProjectionOrchestrator
        )
    }

}