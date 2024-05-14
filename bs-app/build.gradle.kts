plugins {
    alias(libs.plugins.spring)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.flyway)
    id("io.spring.dependency-management") version "1.1.4"
}
dependencies {
    implementation(project(":bs-core"))
    implementation(project(":bs-detail"))
    implementation(project(":eventsource"))
    //	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
//	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//	implementation("org.springframework.boot:spring-boot-starter-jdbc")
//	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation(libs.flyway)
    implementation(libs.bundles.jooq)

	runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(libs.bundles.testcontainers.images)
}

// allow for command line usage of flyway (database migrations). Flyway can also be configured to run on app startup
flyway {
    url = "jdbc:postgresql://localhost:5532/bikeshed"
    user = "postgres"
    password = "postgres"
    driver = "org.postgresql.Driver"
    locations = arrayOf("filesystem:src/main/resources/db/migration")
    schemas = arrayOf("public")

}