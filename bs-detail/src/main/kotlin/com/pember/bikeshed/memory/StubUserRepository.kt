package com.pember.bikedshed.memory

import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.users.UserDetails
import com.pember.bikeshed.core.users.UserRepository

class StubUserRepository: UserRepository {
    override fun maybeFindById(id: UserId): UserDetails {
        return UserDetails(id, "Bob", "bob@bs.com")
    }

}