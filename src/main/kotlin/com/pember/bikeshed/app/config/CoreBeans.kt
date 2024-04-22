package com.pember.bikeshed.app.config

import com.pember.bikeshed.core.users.UserOverviewService
import com.pember.bikeshed.details.StubUserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CoreBeans {

    @Bean
    fun provideUserOverviewService(): UserOverviewService {
        return UserOverviewService(StubUserRepository())
    }
}