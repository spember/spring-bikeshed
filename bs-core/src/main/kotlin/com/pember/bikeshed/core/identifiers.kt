package com.pember.bikeshed.core

import com.pember.eventsource.EntityId

typealias BaseShedId = EntityId<String>


data class UserId(val value: String): BaseShedId(value)
data class BikeId(val value: String): BaseShedId(value)