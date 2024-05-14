package com.pember.bikeshed.core.users

import com.pember.bikeshed.core.UserId
import com.pember.eventsource.DomainEntity
import com.pember.eventsource.Event
import com.pember.eventsource.EventEnvelope
import com.pember.eventsource.errors.UnknownEventException

class User(id: UserId): DomainEntity<UserId>(id) {

    var email: String = ""
        private set

    var name: String = ""
        private set

    var isEmployee: Boolean = false
        private set

    override fun receiveEvent(eventEnvelope: EventEnvelope<UserId, out Event>) {
        when(val event = eventEnvelope.event) {
            is UserCreated -> {
                email = event.email
                name = event.name
            }
            is RoleChanged -> {
                isEmployee = event.employee
            }
            else -> throw UnknownEventException("Unknown event type ${event.javaClass}")
        }
    }
}