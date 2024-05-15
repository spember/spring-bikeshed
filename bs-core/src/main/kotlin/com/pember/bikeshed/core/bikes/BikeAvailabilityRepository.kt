package com.pember.bikeshed.core.bikes

import com.pember.bikeshed.core.BikeId

interface BikeAvailabilityRepository {

    fun getAvailableBikes(): List<BikeId>

    fun addAvailableBike(bikeId: BikeId, currentRevision: Int)

    fun removeAvailableBike(bikeId: BikeId, currentRevision: Int)
}