package com.pember.bikeshed.auth

import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.common.EntityStore
import com.pember.bikeshed.core.users.User
import com.pember.bikeshed.core.users.UserConstraintsRepository
import com.pember.bikeshed.core.users.UserRepository
import org.springframework.stereotype.Service

/**
 * For this fake example, our "auth service" is just a placeholder, and retrieves the same "employee" and "customer"
 * each time
 */
@Service
class PrincipalAuthService(
    private val entityStore: EntityStore<*>,
    private val userConstraintsRepository: UserConstraintsRepository
) {

    fun retrieveCurrentEmployee(): User {
        userConstraintsRepository.listCurrentEmployees().last().let {
            return entityStore.loadCurrentState(User(it))
        }
    }

    fun retrieveCurrentCustomer(): User {
        userConstraintsRepository.listCustomers().last().let {
            return entityStore.loadCurrentState(User(it))
        }
    }
}