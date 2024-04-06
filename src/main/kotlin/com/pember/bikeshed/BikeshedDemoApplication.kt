package com.pember.bikeshed

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BikeshedDemoApplication

fun main(args: Array<String>) {
	runApplication<BikeshedDemoApplication>(*args)
}
