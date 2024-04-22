package com.pember.bikeshed.memory

import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.users.UserDetails
import com.pember.bikeshed.core.users.UserRepository

class StubUserRepository: UserRepository {
    override fun maybeFindById(id: UserId): UserDetails? {
        return UserDetails(id, "Steve", "foo@foo.com")
    }
}