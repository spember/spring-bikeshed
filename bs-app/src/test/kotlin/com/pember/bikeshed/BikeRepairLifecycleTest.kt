package com.pember.bikeshed

import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.RegisterNewBike
import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.bikes.BikeColor
import com.pember.bikeshed.core.bikes.BikeManagementService
import com.pember.bikeshed.support.BaseIntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BikeRepairLifecycleTest: BaseIntegrationTest() {

    @Autowired
    lateinit var bikeManagementService: BikeManagementService



    @Test
    fun `Cannot repair a Bike that doesn't exist`() {
        // Given
        val bikeId = BikeId("fake-bike")
        val user = UserId("stu")

        // When
        assertThrows<IllegalArgumentException> {
            bikeManagementService.initiateRepairs(bikeId, user, "Flat tire")
        }
    }


    @Test
    fun `Repairs mark a bike as unavailable`() {
        // Given
        val bikeId = BikeId("brl-1")
        val user = UserId("manager-stu")

        bikeManagementService.registerNewBike(
            RegisterNewBike(user, bikeId, BikeColor.RED, "Bike Distributors, Co.")
        )

        bikeManagementService.initiateRepairs(bikeId, user, "Flat tire")

        var bike = bikeManagementService.getBikeById(bikeId)!!
        assertEquals(false, bike.available)

        bikeManagementService.completeRepairs(bikeId, user, 100)

        bike = bikeManagementService.getBikeById(bikeId)!!
        assertEquals(true, bike.available)
        assertEquals(1, bike.timesRepaired)
    }

    @Test
    fun `Retiring a bike removes it from inventory`() {
        // Given
        val bikeId = BikeId("brl-2")
        val user = UserId("manager-stu")

        bikeManagementService.registerNewBike(
            RegisterNewBike(user, bikeId, BikeColor.GREEN, "Bike Distributors, Co.")
        )

        bikeManagementService.retireBike(bikeId, user, "Frame cracked")

        val bike = bikeManagementService.getBikeById(bikeId)
        assertEquals(false, bike!!.isActive)
        assertEquals(false, bike.available)
    }
}