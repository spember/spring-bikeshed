plugins {
    id("java")
}
dependencies {
    implementation(project(":bs-core"))
    implementation(libs.jooq)
    implementation(libs.bundles.jackson)
    testImplementation(libs.junit.jupiter)

}