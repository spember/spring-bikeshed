package com.pember.bikeshed.config

import com.pember.bikeshed.core.bikes.BikeAvailabilityRepository
import com.pember.bikeshed.core.bikes.BikeManagementService
import com.pember.bikeshed.core.common.EntityStore
import com.pember.bikeshed.core.reservations.ReservationQueryService
import com.pember.bikeshed.core.reservations.ReservationService
import com.pember.bikeshed.core.reservations.ReservationsQueryModelRepository
import com.pember.bikeshed.core.users.UserConstraintsRepository
import com.pember.bikeshed.core.users.UserRegistrationService
import com.pember.bikeshed.sql.JooqBikeAvailabilityRepository
import com.pember.bikeshed.sql.JooqReservationsQueryModelRepository
import com.pember.bikeshed.sql.JooqUserConstraintsRepository
import com.pember.eventsource.EventRepository
import org.jooq.DSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QueryModelBeans {
    @Bean
    fun provideBikeAvailabilityRepository(dslContext: DSLContext): BikeAvailabilityRepository {
        return JooqBikeAvailabilityRepository(dslContext)
    }

    @Bean
    fun provideBikeManagementService(
        entityStore: EntityStore<*>,
        bikeAvailabilityRepository: BikeAvailabilityRepository
    ): BikeManagementService {

        return BikeManagementService(entityStore, bikeAvailabilityRepository)
    }

    @Bean
    fun provideReservationsQueryModelRepository(dslContext: DSLContext): ReservationsQueryModelRepository {
        return JooqReservationsQueryModelRepository(dslContext)
    }

    @Bean
    fun provideReservationService(entityStore: EntityStore<*>): ReservationService {
        return ReservationService(entityStore)
    }

    @Bean
    fun provideReservationQueryService(
        reservationsQueryModelRepository: ReservationsQueryModelRepository,
        entityStore: EntityStore<*>
    ): ReservationQueryService {
        return ReservationQueryService(reservationsQueryModelRepository, entityStore)
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
        userConstraintsRepository: UserConstraintsRepository,
        entityStore: EntityStore<*>
    ): UserRegistrationService {
        return UserRegistrationService(
            userConstraintsRepository,
            entityStore
        )
    }
}