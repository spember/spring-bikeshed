package com.pember.bikeshed.core.users

import com.pember.bikeshed.core.UserId

/**
 * Used for validating a new user during registration. Mirrors a constraints table which is updated
 * after a user is created
 */
interface UserConstraintsRepository {

    fun isEmailUnique(email: String): Boolean

    fun updateUserConstraints(userId:UserId, updatedEmail: String, isEmployee: Boolean)

    fun getCurrentEmployeeCount(): Int

    fun getCustomerCount(): Int

        
    fun getNextId(isEmployee: Boolean): UserId
}