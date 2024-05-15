package com.pember.bikeshed.core

import com.pember.bikeshed.core.bikes.BikeColor
import java.time.Instant

open class Command(val source: UserId)

class RegisterNewBike(
    employee: UserId,
    val bikeId: BikeId,
    val color: BikeColor,
    val purchasedFrom: String
): Command(employee)




class OpenNewReservation(
    val employee: UserId,
    val customerId: UserId,
    val expectedStartTime: Instant,
    val expectedEndTime: Instant,
    val initialBikes: List<BikeId>
): Command(employee)


class AddBikesToReservation(
    employee: UserId,
    val reservationId: ReservationId,
    val bikeIds: List<BikeId>
): Command(employee)



