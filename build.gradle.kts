import org.jetbrains.kotlin.gradle.tasks.KotlinCompile



buildscript {
	repositories {
		mavenLocal()
		mavenCentral()
	}
	dependencies {
		classpath(libs.postgres.driver)
		classpath("org.flywaydb:flyway-database-postgresql:10.11.1")

	}
}

plugins {
	alias(libs.plugins.kotlin.jvm)
	alias(libs.plugins.flyway)
}

allprojects {
	apply(plugin = "java")
	apply(plugin = "org.jetbrains.kotlin.jvm")

	group = "com.pember"
	version = "0.1.0-SNAPSHOT"

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
		environment("SPRING_PROFILES_ACTIVE", "test")
	}

	dependencies {
	}
}

dependencies {

}



