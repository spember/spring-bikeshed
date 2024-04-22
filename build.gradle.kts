import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
//	id("org.springframework.boot") version "3.2.4"
//	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.23"
//	kotlin("plugin.spring") version "1.9.23"
//	kotlin("plugin.jpa") version "1.9.23"
}

allprojects {
	apply(plugin = "java")
	apply(plugin = "org.jetbrains.kotlin.jvm")

	group = "com.pember"
	version = "0.0.1-SNAPSHOT"

	java {
		sourceCompatibility = JavaVersion.VERSION_21
	}

	repositories {
		mavenLocal()
		mavenCentral()
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs += "-Xjsr305=strict"
			jvmTarget = "21"
		}
	}

	kotlin {
		jvmToolchain(21)
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}

	dependencies {


	}
}

//dependencies {
////	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
////	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
////	implementation("org.springframework.boot:spring-boot-starter-jdbc")
////	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
//	implementation("org.springframework.boot:spring-boot-starter-web")
//	implementation("org.springframework.boot:spring-boot-starter-webflux")
//	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
//
//	implementation("org.jetbrains.kotlin:kotlin-reflect")
//
////	runtimeOnly("org.postgresql:postgresql")
//	testImplementation("org.springframework.boot:spring-boot-starter-test")
//}

