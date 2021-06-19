import org.gradle.api.JavaVersion

object AppConfig {
    const val APPLICATION_ID = "com.alxnophis.jetpackcomposeapp"
    const val COMPILE_SDK_VERSION = 30
    const val BUILD_TOOLS_VERSION = "30.0.3"
    const val MIN_SDK_VERSION = 23
    const val TARGET_SDK_VERSION = 30

    val sourceCompatJavaVersion = JavaVersion.VERSION_11
    val targetCompatJavaVersion = JavaVersion.VERSION_11
    val kotlinOptionJavaVersion = JavaVersion.VERSION_11.toString()

    const val VERSION_CODE = 1
    const val VERSION_NAME = "1.0.0"
}

object Version {
    const val ANDROIDX_APP_COMPAT = "1.2.0"
    const val ANDROIDX_CORE_KTX = "1.3.2"
    const val ANDROIDX_COMPOSE = "1.0.0-beta09"
    const val ANDROIDX_ACTIVITY_COMPOSE = "1.3.0-beta02"
    const val ANDROIDX_ESPRESSO = "3.3.0"
    const val ANDROIDX_LIFECYCLE = "2.3.1"
    const val ANDROIDX_JUNIT = "1.1.2"
    const val GOOGLE_MATERIAL = "1.3.0"
    const val GRADLE = "7.0.0-beta04"
    const val KOTLIN = "1.5.10"
    const val JUNIT = "4.+"
}

object Dep {
    const val ANDROIDX_APP_COMPAT = "androidx.appcompat:appcompat:${Version.ANDROIDX_APP_COMPAT}"
    const val ANDROIDX_ACTIVITY_COMPOSE = "androidx.activity:activity-compose:${Version.ANDROIDX_ACTIVITY_COMPOSE}"
    const val ANDROIDX_COMPOSE_MATERIAL = "androidx.compose.material:material:${Version.ANDROIDX_COMPOSE}"
    const val ANDROIDX_COMPOSE_TEST = "androidx.compose.ui:ui-test-junit4:${Version.ANDROIDX_COMPOSE}"
    const val ANDROIDX_COMPOSE_UI = "androidx.compose.ui:ui:${Version.ANDROIDX_COMPOSE}"
    const val ANDROIDX_COMPOSE_UI_TOOLING = "androidx.compose.ui:ui-tooling:${Version.ANDROIDX_COMPOSE}"
    const val ANDROIDX_CORE_KTX = "androidx.core:core-ktx:${Version.ANDROIDX_CORE_KTX}"
    const val ANDROIDX_ESPRESSO = "androidx.test.espresso:espresso-core: ${Version.ANDROIDX_ESPRESSO}"
    const val ANDROIDX_JUNIT = "androidx.test.ext:junit:{${Version.ANDROIDX_JUNIT}}"
    const val ANDROIDX_LIFECYCLE = "androidx.lifecycle:lifecycle-runtime-ktx:${Version.ANDROIDX_LIFECYCLE}"
    const val GOOGLE_MATERIAL = "com.google.android.material:material:${Version.GOOGLE_MATERIAL}"
    const val KOTLIN_STDLIB = "org.jetbrains.kotlin:kotlin-stdlib:${Version.KOTLIN}"
    const val JUNIT = "junit:junit:${Version.JUNIT}"
}

object Plugin {
    const val GRADLE = "com.android.tools.build:gradle:${Version.GRADLE}"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.KOTLIN}"
}
