package com.pember.bikeshed.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.pember.bikeshed.sql.JooqEventRepository
import com.pember.eventsource.EventRepository
import org.jooq.DSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DetailsConfig {

    @Bean
    fun provideEventRepository(jooq: DSLContext, objectMapper: ObjectMapper): EventRepository<String> {
        return JooqEventRepository(jooq, objectMapper)
    }
}