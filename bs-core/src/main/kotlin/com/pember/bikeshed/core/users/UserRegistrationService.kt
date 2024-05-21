package com.pember.bikeshed.core.users

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
    fun registerNewUser(name: String, email: String,
                        isEmployee: Boolean, agent: UserId): UserId {

        if (name.isEmpty() || email.isEmpty()) {
            throw IllegalArgumentException("Name and email must not be blank")
        }

        if (!userConstraintsRepository.isEmailUnique(email)) {
            log.warn("User ${agent.value} attempted to register a user " +
                    "with a non-unique email: $email")
            throw IllegalArgumentException("Email is not unique")
        }

        val userId = userConstraintsRepository.getNextId(isEmployee)
        val ewe = EntityWithEvents(User(userId), agent.value)
            .apply(
                UserCreated(name, email),
                RoleChanged(isEmployee)
            )

        entityStore.persist(ewe)
        return userId
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserRegistrationService::class.java)
    }
}