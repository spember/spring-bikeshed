plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") { from(files("libs.versions.toml")) }
    }
}
rootProject.name = "bikeshed"
include("eventsource")
include("bs-core")
include("bs-app")
include("bs-detail")
