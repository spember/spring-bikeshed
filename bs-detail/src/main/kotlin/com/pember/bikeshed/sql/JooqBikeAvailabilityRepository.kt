package com.pember.bikeshed.sql

import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.bikes.BikeAvailabilityRepository
import com.pember.bikeshed.db.jooq.tables.AvailableBikes
import org.jooq.DSLContext

class JooqBikeAvailabilityRepository(private val jooq: DSLContext): BikeAvailabilityRepository {

    override fun getAvailableBikes(): List<BikeId> {
        return jooq.selectFrom(AvailableBikes.AVAILABLE_BIKES)
            .fetch()
            .map { BikeId(it.bikeId) }
    }

    override fun addAvailableBike(bikeId: BikeId, currentRevision: Int) {
        jooq.insertInto(AvailableBikes.AVAILABLE_BIKES)
            .columns(
                AvailableBikes.AVAILABLE_BIKES.BIKE_ID,
                AvailableBikes.AVAILABLE_BIKES.REVISION
            )
            .values(
                bikeId.value,
                currentRevision
            )
            .onConflictDoNothing()
            .execute()
    }

    override fun removeAvailableBike(bikeId: BikeId, currentRevision: Int) {
        jooq.deleteFrom(AvailableBikes.AVAILABLE_BIKES)
            .where(AvailableBikes.AVAILABLE_BIKES.BIKE_ID.eq(bikeId.value))
            .and(AvailableBikes.AVAILABLE_BIKES.REVISION.eq(currentRevision-1))
            .execute()
    }
}
