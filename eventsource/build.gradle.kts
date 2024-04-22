plugins {
    // Apply the java-library plugin for API and implementation separation.
    id ("java-library")
}
dependencies {
    implementation("org.reflections:reflections:0.10.2")
}
