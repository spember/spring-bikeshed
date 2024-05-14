package com.pember.bikeshed.core.bikes

import com.pember.eventsource.EventRepository

/**
 * Sometimes we have to do a lot of things on Bikes at once. This service is for that.
 */
class BulkBikeOperationsService(private val eventRepository: EventRepository<String>) {

    fun handleABigShipment() {

    }
}