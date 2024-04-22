package com.pember.bikeshed.core.users

import com.pember.bikeshed.core.UserId

sealed class UserResponse {
    data class FoundUser(val id: UserId, val name: String) : UserResponse()
    data class UserNotFound(val id: UserId) : UserResponse()
}