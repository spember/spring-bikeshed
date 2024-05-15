package com.pember.bikeshed.core.common

import com.pember.bikeshed.core.BaseShedId
import com.pember.eventsource.DomainEntity
import com.pember.eventsource.EntityId
import com.pember.eventsource.EntityWithEvents
import com.pember.eventsource.Event
import com.pember.eventsource.EventEnvelope
import com.pember.eventsource.EventRepository
import java.util.function.Consumer

abstract class EntityStore<TX>(private val eventRepository: EventRepository<String>) {

    protected var asyncCompleteHandler = Consumer{ _:Int ->}
        private set

    abstract fun <EI: BaseShedId, DE: DomainEntity<EI>>persist(ewe: EntityWithEvents<EI, DE>)

    abstract fun persistMultiple(ewes: List<EntityWithEvents<out EntityId<String>, out DomainEntity<out EntityId<String>>>>)

    fun <EI : EntityId<String>, DE : DomainEntity<EI>> loadCurrentState(domainEntity: DE): DE {
        eventRepository.loadForId(domainEntity.id)
            .forEach(Consumer<EventEnvelope<EI, Event>> { eventEnvelope: EventEnvelope<EI, Event> ->
                domainEntity.apply(eventEnvelope)
            })
        return domainEntity
    }

    /**
     * Load the current state for multiple entities of the same class
     */
    fun <EI : EntityId<String>, DE : DomainEntity<EI>> loadCurrentState(entities: List<DE>): List<DE> {
        eventRepository.loadForIds(entities.map { it.id })
            .forEach { eventEnvelope: EventEnvelope<EI, Event> ->
                // continuously searching the list is not super-performant... but this is just a demo!
                entities.find { it.id == eventEnvelope.entityId }?.apply(eventEnvelope)
            }
        return entities
    }

    fun onAsyncComplete(handler: Consumer<Int>) {
        asyncCompleteHandler = handler
    }

}