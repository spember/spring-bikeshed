import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property


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
//	alias(libs.plugins.jooq)
	alias(libs.plugins.kotlin.jvm)
	alias(libs.plugins.flyway)
	id( "nu.studer.jooq") version "9.0"
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
	jooqGenerator(libs.postgres.driver)
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


jooq {
	edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)  // default (can be omitted)

	configurations {
		create("main") {  // name of the jOOQ configuration
			generateSchemaSourceOnCompilation.set(true)  // default (can be omitted)

			jooqConfiguration.apply {
				logging = Logging.WARN
				jdbc.apply {
					driver = "org.postgresql.Driver"
					url = "jdbc:postgresql://localhost:5532/bikeshed"
					user = "postgres"
					password = "postgres"
					properties.add(Property().apply {
						key = "ssl"
						value = "false"
					})
				}
				generator.apply {
					name = "org.jooq.codegen.DefaultGenerator"
					database.apply {
						name = "org.jooq.meta.postgres.PostgresDatabase"
						inputSchema = "public"
						includes = ".*"
						excludes = ""
					}
					generate.apply {
						isDeprecated = false
						isRecords = true
						isImmutablePojos = true
						isFluentSetters = true
					}
					target.apply {
						packageName = "com.pember.bikeshed.db.jooq"
						directory = "bs-details/build/generated-src/main/gen"  // default (can be omitted)
					}
					strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
				}
			}
		}
	}
}
