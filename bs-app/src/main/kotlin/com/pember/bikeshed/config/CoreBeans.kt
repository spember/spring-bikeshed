package com.pember.bikeshed.config

import com.pember.bikedshed.memory.StubUserRepository
import com.pember.bikeshed.core.bikes.BikeManagementService
import com.pember.bikeshed.core.common.EntityStore
import com.pember.bikeshed.core.projections.ProjectionOrchestrator
import com.pember.bikeshed.core.reservations.ReservationService
import com.pember.bikeshed.core.users.UserConstraintsRepository
import com.pember.bikeshed.core.users.UserOverviewService
import com.pember.bikeshed.core.users.UserRegistrationService
import com.pember.bikeshed.sql.JooqEntityStore
import com.pember.bikeshed.sql.JooqEventRepository
import com.pember.bikeshed.sql.JooqProjectionOrchestrator
import com.pember.bikeshed.sql.JooqUserConstraintsRepository
import com.pember.eventsource.EntityLoader
import com.pember.eventsource.EventRegistry
import com.pember.eventsource.EventRepository
import org.jooq.DSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CoreBeans {

    @Bean
    fun provideUserOverviewService(): UserOverviewService {
        return UserOverviewService(StubUserRepository())
    }

    @Bean
    fun provideBikeManagementService(
        eventRepository: EventRepository<String>,
        entityLoader: EntityLoader<String>
    ): BikeManagementService {

        return BikeManagementService(eventRepository, entityLoader)
    }

    @Bean
    fun provideReservationService(entityStore: EntityStore<*>): ReservationService {
        return ReservationService(entityStore)
    }


    @Bean
    fun provideProjectionOrchestrator(): ProjectionOrchestrator<*> {
        return JooqProjectionOrchestrator()
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
    fun provideUserConstraintsRepository(
        dslContext: DSLContext
    ): UserConstraintsRepository {
        return JooqUserConstraintsRepository(dslContext)
    }

    @Bean
    fun provideUserRegistrationService(
        eventRepository: EventRepository<String>,
        entityLoader: EntityLoader<String>,
        userConstraintsRepository: UserConstraintsRepository,
        entityStore: EntityStore<*>
    ): UserRegistrationService {
        return UserRegistrationService(
            eventRepository,
            entityLoader,
            userConstraintsRepository,
            entityStore
        )
    }

    @Bean
    fun provideEventRegistry(): EventRegistry {
        val registry = EventRegistry()
        registry.scan("com.pember.bikeshed.core")
        return registry
    }

    @Bean
    fun provideEntityLoader(eventRepository: EventRepository<String>): EntityLoader<String> {
        return EntityLoader(eventRepository);
    }
}