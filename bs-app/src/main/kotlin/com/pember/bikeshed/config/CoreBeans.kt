package com.pember.bikeshed.config

import com.pember.bikeshed.core.bikes.BikeManagementService
import com.pember.bikeshed.core.users.UserOverviewService
import com.pember.bikeshed.memory.StubUserRepository
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
}