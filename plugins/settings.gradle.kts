dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    // Help link: https://docs.gradle.org/7.3.3/userguide/platforms.html#sub:central-declaration-of-dependencies
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
