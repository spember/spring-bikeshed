package com.pember.bikeshed.core.users

import com.pember.bikeshed.core.RegisterNewUser
import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.common.EntityStore
import com.pember.eventsource.EntityWithEvents
import org.slf4j.LoggerFactory

class UserRegistrationService(
    private val userConstraintsRepository: UserConstraintsRepository,
    private val entityStore: EntityStore<*>
) {

    /**
     * Attempt to register a new user with the given name and email. Will check the Constraints repository
     * as part of the process, and both update that and insert a new user if successful.
     */
    fun process(command: RegisterNewUser): UserId {

        if (command.name.isEmpty() || command.email.isEmpty()) {
            throw IllegalArgumentException("Name and email must not be blank")
        }

        if (!userConstraintsRepository.isEmailUnique(command.email)) {
            log.warn("User ${command.agent.value} attempted to register a user " +
                    "with a non-unique email: $command.email")
            throw IllegalArgumentException("Email is not unique")
        }

        val userId = userConstraintsRepository.getNextId(command.isEmployee)
        val ewe = EntityWithEvents(User(userId), command.agent.value)
            .apply(
                UserCreated(command.name, command.email),
                RoleChanged(command.isEmployee)
            )

        entityStore.persist(ewe)
        return userId
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserRegistrationService::class.java)
    }
}