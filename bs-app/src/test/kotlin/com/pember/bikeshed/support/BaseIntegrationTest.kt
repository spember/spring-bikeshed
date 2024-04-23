package com.pember.bikeshed.support

import org.junit.ClassRule
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.time.Duration

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseIntegrationTest {

    @LocalServerPort
    protected var serverPort: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    companion object {

        @JvmStatic
        val environment: DockerComposeContainer<*> = DockerComposeContainer(
            File("src/test/resources/docker-compose-test.yaml")
        )
            .withExposedService("test-db", 5432)
            .withExposedService("test-localstack", 4566)


        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            environment.start()
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            environment.stop()
        }

    }

}