package com.pember.bikeshed.core.users

import com.pember.bikeshed.core.UserId
import org.slf4j.LoggerFactory

class UserOverviewService(private val userRepository: UserRepository) {

    fun retrieveUser(id: UserId): UserResponse {
        val maybeUser = userRepository.maybeFindById(id)
        return if (maybeUser == null) {
            log.warn("Could not find a user with ID: $id")
            UserResponse.UserNotFound(id)
        } else {
            UserResponse.FoundUser(maybeUser.id, maybeUser.name)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserOverviewService::class.java)
    }
}