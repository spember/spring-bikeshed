plugins {
    // Apply the java-library plugin for API and implementation separation.
    id ("java-library")
}
dependencies {
    implementation(libs.reflections)
}
