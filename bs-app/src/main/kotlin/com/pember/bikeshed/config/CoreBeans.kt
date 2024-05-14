package com.pember.bikeshed.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.pember.bikedshed.memory.StubUserRepository
import com.pember.bikeshed.core.bikes.BikeManagementService
import com.pember.bikeshed.core.projections.ProjectionOrchestrator
import com.pember.bikeshed.core.reservations.ReservationPersistenceOrchestrator
import com.pember.bikeshed.core.reservations.ReservationService
import com.pember.bikeshed.core.users.UserConstraintsRepository
import com.pember.bikeshed.core.users.UserOverviewService
import com.pember.bikeshed.core.users.UserPersistenceOrchestrator
import com.pember.bikeshed.core.users.UserRegistrationService
import com.pember.bikeshed.sql.JooqEventRepository
import com.pember.bikeshed.sql.JooqProjectionOrchestrator
import com.pember.bikeshed.sql.JooqReservationPersistenceOrchestrator
import com.pember.bikeshed.sql.JooqUserConstraintsRepository
import com.pember.bikeshed.sql.JooqUserPersistenceOrchestrator
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
    fun provideReservationPersistenceOrchestrator(
        dslContext: DSLContext,
        eventRepository: EventRepository<String>
    ): ReservationPersistenceOrchestrator {
        return JooqReservationPersistenceOrchestrator(dslContext, eventRepository as JooqEventRepository)
    }


    @Bean
    fun provideReservationService(reservationPersistenceOrchestrator: ReservationPersistenceOrchestrator): ReservationService {
        return ReservationService(reservationPersistenceOrchestrator)
    }


    @Bean
    fun provideUserPersistenceOrchestrator(
        dslContext: DSLContext,
        eventRepository: EventRepository<String>,
        projectionOrchestrator: ProjectionOrchestrator<*>
    ): UserPersistenceOrchestrator {
        return JooqUserPersistenceOrchestrator(
            dslContext,
            eventRepository as JooqEventRepository,
            projectionOrchestrator as JooqProjectionOrchestrator
        )
    }

    @Bean
    fun provideProjectionOrchestrator(): ProjectionOrchestrator<*> {
        return JooqProjectionOrchestrator()
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
        userPersistenceOrchestrator: UserPersistenceOrchestrator
    ): UserRegistrationService {
        return UserRegistrationService(
            eventRepository,
            entityLoader,
            userConstraintsRepository,
            userPersistenceOrchestrator
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