package com.pember.bikeshed.core.users

import com.pember.bikeshed.core.UserId

interface UserRepository {

    fun maybeFindById(id: UserId): User?
}