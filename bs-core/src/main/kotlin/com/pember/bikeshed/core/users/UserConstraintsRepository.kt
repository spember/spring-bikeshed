package com.pember.bikeshed.core.users

import com.pember.bikeshed.core.UserId

/**
 * Used for validating a new user during registration. Mirrors a constraints table which is updated
 * after a user is created
 */
interface UserConstraintsRepository {

    fun isEmailUnique(email: String): Boolean

    fun updateUserConstraints(userId:UserId, updatedEmail: String, isEmployee: Boolean)

    fun updateUserRole(userId: UserId, isEmployee: Boolean)

    fun getCurrentEmployeeCount(): Int

    fun listCurrentEmployees(): List<UserId>

    fun getCustomerCount(): Int

    fun listCustomers(): List<UserId>

    fun getNextId(isEmployee: Boolean): UserId
}