package com.pember.bikeshed.core.users

import com.pember.bikeshed.core.UserId

data class UserDetails(val id: UserId, val name: String, val email: String) {
    init {
        require(name.isNotBlank()) { "Name must not be blank" }
        require(email.isNotBlank()) { "Email must not be blank" }
    }
}