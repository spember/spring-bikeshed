package com.pember.bikeshed

import com.pember.bikeshed.core.BikeId
import com.pember.bikeshed.core.RegisterNewBike
import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.bikes.BikeColor
import com.pember.bikeshed.core.bikes.BikeManagementService
import com.pember.bikeshed.support.BaseIntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class BasicBikeControllerTest: BaseIntegrationTest() {

    @Autowired
    lateinit var bikeManagementService: BikeManagementService

    @Test
    fun `Looking up a bike by bad Id should be a 404`() {
        val response = restTemplate.getForEntity("http://localhost:$serverPort/bikes/123", String::class.java)
        assertEquals(404, response.statusCode.value())
        assertEquals(5,5)
    }

    @Test
    fun `Looking a valid bike should return details` () {
        val bikeId = BikeId("AZ-123")

        bikeManagementService.registerNewBike(RegisterNewBike(
            UserId("stu"), bikeId, BikeColor.RED, "Bike Distributors, Co.")
        )

        val response = restTemplate.getForEntity("http://localhost:$serverPort/bikes/${bikeId.value}", String::class.java)
        println(response.body)
        assertEquals(200, response.statusCode.value())

    }
}