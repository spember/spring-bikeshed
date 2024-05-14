package com.pember.bikeshed.core.common

import com.pember.bikeshed.core.BaseShedId
import com.pember.eventsource.DomainEntity
import com.pember.eventsource.EntityWithEvents

abstract class EntityStore<TX> {

    abstract fun <EI: BaseShedId, DE: DomainEntity<EI>>persist(ewe: EntityWithEvents<EI, DE>)

}