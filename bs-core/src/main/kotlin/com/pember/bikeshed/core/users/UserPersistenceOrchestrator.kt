package com.pember.bikeshed.core.users

import com.pember.bikeshed.core.UserId
import com.pember.eventsource.EntityWithEvents

/**
 * Sometimes a simple, single Repository is not enough.
 * An Orchestrator should be used as a way to coordinate multiple Repositories for persistence, in which
 * ever method is best suited for our database model (e.g. transactions in sql)
 */
interface UserPersistenceOrchestrator {

    fun storeNewUser(
        entityWithEvents: EntityWithEvents<UserId, User>
    )
}