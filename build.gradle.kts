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
	}

	dependencies {
	}
}

dependencies {

}

// allow for command line usage of flyway (database migrations). Flyway can also be configured to run on app startup
flyway {
    url = "jdbc:postgresql://localhost:5532/bikeshed"
    user = "postgres"
	password = "postgres"
	driver = "org.postgresql.Driver"
	locations = arrayOf("filesystem: bs-app / src / main / resources / db / migration")
	schemas = arrayOf("public")

}



