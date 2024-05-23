package com.pember.bikeshed.core.users

import com.pember.eventsource.Event

/*
Because these events are not marked with an EventAlias, we will receive runtime warnings to do so.
 */

data class UserCreated(val name: String, val email: String): Event

data class RoleChanged(val employee: Boolean): Event