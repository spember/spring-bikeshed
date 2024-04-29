import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property

plugins {
    id("java")
    id( "nu.studer.jooq") version "9.0"
}

dependencies {
    implementation(project(":bs-core"))
    implementation(libs.jooq)
    implementation(libs.bundles.jackson)
    testImplementation(libs.junit.jupiter)
    jooqGenerator(libs.postgres.driver)

}

sourceSets {
    main {
        java.srcDir("build/generated-src/main/java")
    }
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
                        directory = "build/generated-src/main/java"  // default (can be omitted)
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}