include(":app")
// Shared modules
include(":shared:api")
include(":shared:core")
include(":shared:kotlin")
include(":shared:router")
include(":shared:testing")
// Feature modules
include(":feature:root")
include(":feature:authentication")
include(":feature:game:ballclicker")
include(":feature:home")
include(":feature:location-tracker")
include(":feature:posts")
include(":feature:settings")
include(":feature:spacex")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        includeBuild("plugins")
    }
}

enableFeaturePreview("VERSION_CATALOGS")
