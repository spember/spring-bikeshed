package com.pember.bikeshed.auth

import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.users.User
import com.pember.bikeshed.core.users.UserRepository
import org.springframework.stereotype.Service

/**
 * For this fake example, our "auth service" is just a placeholder, and retrieves the same "employee" and "customer"
 * each time
 */
@Service
class PrincipalAuthService() {

    fun retrieveCurrentEmployee(): User {
        val employee = User(UserId("e01"))
        return employee
    }
}