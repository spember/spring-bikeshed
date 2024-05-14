package com.pember.bikeshed.core.users

import com.pember.eventsource.Event

data class UserCreated(val name: String, val email: String): Event

data class RoleChanged(val employee: Boolean): Event