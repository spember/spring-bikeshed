package com.pember.bikeshed.core

import com.pember.bikeshed.core.bikes.BikeColor
import java.time.Instant

open class Command(val agent: UserId, val occurredAt: Instant)


class RegisterNewUser(
    employee: UserId,
    val name: String,
    val email: String,
    val isEmployee: Boolean,
    occurredAt: Instant = Instant.now()
): Command(employee, occurredAt)


class RegisterNewBike(
    employee: UserId,
    val bikeId: BikeId,
    val color: BikeColor,
    val purchasedFrom: String,
    occurredAt: Instant = Instant.now()
): Command(employee, occurredAt)

class OpenNewReservation(
    val employee: UserId,
    val customerId: UserId,
    val expectedStartTime: Instant,
    val expectedEndTime: Instant,
    occurredAt: Instant = Instant.now()
): Command(employee, occurredAt)


class AddBikesToReservation(
    employee: UserId,
    val reservationId: ReservationId,
    val bikeIds: List<BikeId>,
    occurredAt: Instant = Instant.now()
): Command(employee, occurredAt)

class CompleteReservation(
    employee: UserId,
    val reservationId: ReservationId,
    occurredAt: Instant = Instant.now()
): Command(employee, occurredAt)


