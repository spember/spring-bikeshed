package com.pember.bikeshed.core

import com.pember.bikeshed.core.bikes.BikeColor

open class Command(val source: UserId)

class RegisterNewBike(
    employee: UserId,
    val bikeId: BikeId,
    val color: BikeColor,
    val purchasedFrom: String
): Command(employee)





