package com.pember.bikeshed.config

import com.pember.bikedshed.memory.StubUserRepository
import com.pember.bikeshed.core.bikes.BikeManagementService
import com.pember.bikeshed.core.reservations.ReservationService
import com.pember.bikeshed.core.users.UserOverviewService
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
    fun provideBikeManagementService(dslContext: DSLContext, eventRepository: EventRepository<String>): BikeManagementService {
        println(dslContext.selectOne())
        return BikeManagementService(eventRepository)
    }

    @Bean
    fun provideReservationService(eventRepository: EventRepository<String>): ReservationService {
        return ReservationService(eventRepository)
    }

    @Bean
    fun provideEventRegistry(): EventRegistry {
        val registry = EventRegistry()
        registry.scan("com.pember.bikeshed.core")
        return registry
    }
}