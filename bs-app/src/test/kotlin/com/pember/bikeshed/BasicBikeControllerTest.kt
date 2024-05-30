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
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.GET
import org.springframework.http.MediaType.APPLICATION_JSON

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BasicBikeControllerTest: BaseIntegrationTest() {

    @Autowired
    lateinit var bikeManagementService: BikeManagementService

    @Test
    fun `Looking up a bike by bad Id should be a 404`() {

        val headers = HttpHeaders()
        headers.contentType = APPLICATION_JSON
        val entity = HttpEntity<String>(headers)
        val response = restTemplate.exchange("http://localhost:$serverPort/bike/123", GET, entity, String::class.java)
        assertEquals(404, response.statusCode.value())
        assertEquals(5,5)
    }

    @Test
    fun `Looking up a valid bike should return details` () {
        val bikeId = BikeId("AZ-123")

        bikeManagementService.process(RegisterNewBike(
            UserId("stu"), bikeId, BikeColor.RED, "Bike Distributors, Co.")
        )
        Thread.sleep(200)

        val headers = HttpHeaders()
        headers.contentType = APPLICATION_JSON
        val entity = HttpEntity<String>(headers)
        val response = restTemplate.exchange("http://localhost:$serverPort/bikes/${bikeId.value}", GET, entity, String::class.java)
        println(response.body)
        assertEquals(200, response.statusCode.value())

    }
}