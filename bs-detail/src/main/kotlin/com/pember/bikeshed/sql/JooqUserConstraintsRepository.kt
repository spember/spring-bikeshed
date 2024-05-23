package com.pember.bikeshed.sql

import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.users.UserConstraintsRepository
import com.pember.bikeshed.db.jooq.tables.UserConstraints.USER_CONSTRAINTS
import org.jooq.DSLContext

class JooqUserConstraintsRepository(private val jooq: DSLContext): UserConstraintsRepository {
    override fun isEmailUnique(email: String): Boolean {
        return jooq.selectFrom(USER_CONSTRAINTS)
            .where(USER_CONSTRAINTS.EMAIL.eq(email))
            .fetchOne() == null
    }

    override fun updateUserConstraints(userId: UserId, updatedEmail: String, isEmployee: Boolean) {
        // todo: add revision here, we don't want to update old user constraints
        jooq.deleteFrom(USER_CONSTRAINTS)
            .where(USER_CONSTRAINTS.USER_ID.eq(userId.value))
            .execute()
        jooq.insertInto(USER_CONSTRAINTS)
            .columns(
                USER_CONSTRAINTS.USER_ID,
                USER_CONSTRAINTS.EMAIL,
                USER_CONSTRAINTS.IS_EMPLOYEE
            )
            .values(
                userId.value,
                updatedEmail,
                isEmployee
            )
            .execute()
    }

    override fun updateUserRole(userId: UserId, isEmployee: Boolean) {
        jooq.update(USER_CONSTRAINTS)
            .set(USER_CONSTRAINTS.IS_EMPLOYEE, isEmployee)
            .where(USER_CONSTRAINTS.USER_ID.eq(userId.value))
            .execute()
    }

    override fun getCurrentEmployeeCount(): Int {
        return jooq.selectFrom(USER_CONSTRAINTS)
            .where(USER_CONSTRAINTS.IS_EMPLOYEE.eq(true))
            .fetch()
            .size
    }

    override fun listCurrentEmployees(): List<UserId> = fetchUserIds(true)
    override fun listCustomers(): List<UserId> = fetchUserIds(false)

    private fun fetchUserIds(isEmployee: Boolean) =
        jooq.selectFrom(USER_CONSTRAINTS)
            .where(USER_CONSTRAINTS.IS_EMPLOYEE.eq(isEmployee))
            .fetch()
            .map { UserId(it.userId) }

    override fun getCustomerCount(): Int {
        return jooq.selectFrom(USER_CONSTRAINTS)
            .where(USER_CONSTRAINTS.IS_EMPLOYEE.eq(false))
            .fetch()
            .size
    }

    override fun getNextId(isEmployee: Boolean): UserId {
        return if (isEmployee) {
            UserId("e-${getCurrentEmployeeCount()+1}")
        } else {
            UserId("c-${getCustomerCount()+1}")
        }
    }
}