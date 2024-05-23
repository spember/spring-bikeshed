package com.pember.bikeshed

import com.pember.bikeshed.core.RegisterNewUser
import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.common.EntityStore
import com.pember.bikeshed.core.users.User
import com.pember.bikeshed.core.users.UserRegistrationService
import com.pember.bikeshed.support.BaseIntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant
import java.time.temporal.ChronoUnit

class UserRegistrationTest: BaseIntegrationTest() {

    @Autowired
    private lateinit var userRegistrationService: UserRegistrationService

    @Autowired
    private lateinit var entityStore: EntityStore<*>

    @Test
    fun `registering a new user should work great` () {

        val id = userRegistrationService.process(
            RegisterNewUser(
                UserId("system"),
                "Stu Nichols",
                "stu@bikeshed.com",
                true
            )
        )
        assertNotNull(id)

        val user = entityStore.loadCurrentState(User(id))
        assertEquals(id, user.id)
        assertEquals("Stu Nichols", user.name)
        assertEquals(true, user.isEmployee)
    }

    @Test
    fun `registering a user with the same email should not work`() {
        val bossId = userRegistrationService.process(RegisterNewUser(
            UserId("system"),
            "Boss Sam", "sam@bikeshed.com", true,
            occurredAt = Instant.now().minus(5, ChronoUnit.DAYS)
        ))

        val customerId = userRegistrationService.process(
            RegisterNewUser(bossId,"Jane Smith", "janesmith@testmail.com", false )
        )

        assertThrows<IllegalArgumentException> {
            userRegistrationService.process(
                RegisterNewUser(bossId, "Joan Smith", "janesmith@testmail.com", false)
            )
        }

        val boss = entityStore.loadCurrentState(User(bossId))
        assertEquals(boss.name, "Boss Sam")
        assertTrue(boss.isEmployee)

        val jane = entityStore.loadCurrentState(User(customerId))
        assertEquals("Jane Smith", jane.name)

    }
}