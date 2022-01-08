@file:Suppress("unused", "MemberVisibilityCanBePrivate", "SpellCheckingInspection")

import org.gradle.api.JavaVersion

object AppVersion {
    private const val MAJOR = 0
    private const val MINOR = 0
    private const val BUILD = 1

    const val NAME: String = "${MAJOR}.${MINOR}.${BUILD}"
    val CODE = "${MAJOR}${MINOR.format()}${BUILD.format()}".toInt()

    private fun Int.format() = "%03d".format(this)
}

object AppConfig {
    const val APPLICATION_ID = "com.alxnophis.jetpack"
    const val APP_PACKAGE_NAME = APPLICATION_ID
    const val APP_PACKAGE_NAME_DEBUG = "$APPLICATION_ID.debug"
    const val COMPILE_SDK_VERSION = 31
    const val BUILD_TOOLS_VERSION = "31.0.0"
    const val MIN_SDK_VERSION = 23
    const val TARGET_SDK_VERSION = 31

    val sourceCompat = JavaVersion.VERSION_11
    val targetCompat = JavaVersion.VERSION_11
    val kotlinOption = JavaVersion.VERSION_11
}

object Modules {
    const val APP = ":app"

    const val FEATURE_AUTHENTICATION = ":feature:authentication"

    const val SHARED_CORE = ":shared:core"
    const val SHARED_ROUTER = ":shared:router"
    const val SHARED_TESTING = ":shared:testing"
}

private object Version {
    const val ANDROIDX_ANNOTATIONS = "1.2.0"
    const val ANDROIDX_ACTIVITY = "1.3.1"
    const val ANDROIDX_APPCOMPAT = "1.3.1"
    const val ANDROIDX_ARCH_CORE_COMMON = "2.1.0"
    const val ANDROIDX_ARCH_CORE_RUNTIME = "2.1.0"
    const val ANDROIDX_COMPOSE = "1.0.5"
    const val ANDROID_COMPOSE_CONSTRAINT_LAYOUT = "1.0.0-rc02"
    const val ANDROIDX_COMPOSE_NAVIGATION = "2.4.0-rc01"
    const val ANDROIDX_CONSTRAINT_LAYOUT = "2.1.1"
    const val ANDROIDX_CORE_KTX = "1.6.0"
    const val ANDROIDX_LIFECYCLE = "2.4.0"
    const val ANDROIDX_LIFECYCLE_EXTENSIONS = "2.2.0"
    const val ANDROIDX_ORCHESTRATOR = "1.3.0"
    const val ANDROIDX_TEST_ARCH_CORE = "2.1.0"
    const val ANDROIDX_TEST_JUNIT = "1.1.3"
    const val ANDROIDX_TEST_RULES = "1.4.0"
    const val ANDROIDX_TEST_RUNNER = "1.3.0"
    const val ANDROIDX_VECTOR_DRAWABLE = "1.0.1"
    const val ANDROIDX_SECURITY_CRYPTO_VERSION = "1.0.0"
    const val ARROW = "1.0.1"
    const val COROUTINES = "1.5.2"
    const val GOOGLE_ANDROID_MATERIAL = "1.4.0"
    const val JUNIT = "4.13.2"
    const val JUNIT_JUPITER = "5.8.2"
    const val KOIN = "3.1.4"
    const val MOSHI = "1.12.0"
    const val MOCKITO = "4.0.0"
    const val LEAK_CANARY = "2.7"
    const val PERMISSION_DISPATCHER = "4.9.1"
    const val TIMBER = "5.0.1"
    const val TURBINE = "0.7.0"
}

object Dep {
    const val ANDROIDX_ANNOTATIONS = "androidx.annotation:annotation:${Version.ANDROIDX_ANNOTATIONS}"
    const val ANDROIDX_APPCOMPAT = "androidx.appcompat:appcompat:${Version.ANDROIDX_APPCOMPAT}"
    const val ANDROIDX_ACTIVITY = "androidx.activity:activity-ktx:${Version.ANDROIDX_ACTIVITY}"
    const val ANDROIDX_ARCH_CORE_COMMON = "androidx.arch.core:core-common:${Version.ANDROIDX_ARCH_CORE_COMMON}"
    const val ANDROIDX_ARCH_CORE_RUNTIME = "androidx.arch.core:core-runtime:${Version.ANDROIDX_ARCH_CORE_RUNTIME}"
    const val ANDROIDX_ARCH_CORE_TESTING = "androidx.arch.core:core-testing:${Version.ANDROIDX_TEST_ARCH_CORE}"
    const val ANDROIDX_CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:${Version.ANDROIDX_CONSTRAINT_LAYOUT}"
    const val ANDROIDX_CORE_KTX = "androidx.core:core-ktx:${Version.ANDROIDX_CORE_KTX}"
    const val ANDROIDX_COMPOSE_ACTIVITY = "androidx.activity:activity-compose:${Version.ANDROIDX_ACTIVITY}"
    const val ANDROIDX_COMPOSE_ANIMATION = "androidx.compose.animation:animation:${Version.ANDROIDX_COMPOSE}"
    const val ANDROIDX_COMPOSE_CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout-compose:${Version.ANDROID_COMPOSE_CONSTRAINT_LAYOUT}"
    const val ANDROIDX_COMPOSE_FOUNDATION = "androidx.compose.foundation:foundation:${Version.ANDROIDX_COMPOSE}"
    const val ANDROIDX_COMPOSE_FOUNDATION_LAYOUT = "androidx.compose.foundation:foundation-layout:${Version.ANDROIDX_COMPOSE}"
    const val ANDROIDX_COMPOSE_MATERIAL = "androidx.compose.material:material:${Version.ANDROIDX_COMPOSE}"
    const val ANDROIDX_COMPOSE_MATERIAL_ICONS_CORE = "androidx.compose.material:material-icons-core:${Version.ANDROIDX_COMPOSE}"
    const val ANDROIDX_COMPOSE_MATERIAL_ICONS_EXTENDED = "androidx.compose.material:material-icons-extended:${Version.ANDROIDX_COMPOSE}"
    const val ANDROIDX_COMPOSE_NAVIGATION = "androidx.navigation:navigation-compose:${Version.ANDROIDX_COMPOSE_NAVIGATION}"
    const val ANDROIDX_COMPOSE_RUNTIME = "androidx.compose.runtime:runtime:${Version.ANDROIDX_COMPOSE}"
    const val ANDROIDX_COMPOSE_RUNTIME_LIVEDATA = "androidx.compose.runtime:runtime-livedata:${Version.ANDROIDX_COMPOSE}"
    const val ANDROIDX_COMPOSE_UI = "androidx.compose.ui:ui:${Version.ANDROIDX_COMPOSE}"
    const val ANDROIDX_COMPOSE_UI_TEST = "androidx.compose.ui:ui-test-junit4:${Version.ANDROIDX_COMPOSE}"
    const val ANDROIDX_COMPOSE_UI_TOOLING = "androidx.compose.ui:ui-tooling:${Version.ANDROIDX_COMPOSE}"
    const val ANDROIDX_COMPOSE_UI_TOOLING_PREVIEW = "androidx.compose.ui:ui-tooling-preview:${Version.ANDROIDX_COMPOSE}"
    const val ANDROIDX_COMPOSE_VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel-compose:${Version.ANDROIDX_LIFECYCLE}"
    const val ANDROIDX_LIFECYCLE_COMMON = "androidx.lifecycle:lifecycle-common-java8:${Version.ANDROIDX_LIFECYCLE}"
    const val ANDROIDX_LIFECYCLE_COMPILER = "androidx.lifecycle:lifecycle-compiler:${Version.ANDROIDX_LIFECYCLE}"
    const val ANDROIDX_LIFECYCLE_EXTENSION = "androidx.lifecycle:lifecycle-extensions:${Version.ANDROIDX_LIFECYCLE_EXTENSIONS}"
    const val ANDROIDX_LIFECYCLE_LIVEDATA = "androidx.lifecycle:lifecycle-livedata-ktx:${Version.ANDROIDX_LIFECYCLE}"
    const val ANDROIDX_LIFECYCLE_RUNTIME = "androidx.lifecycle:lifecycle-runtime-ktx:${Version.ANDROIDX_LIFECYCLE}"
    const val ANDROIDX_LIFECYCLE_VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Version.ANDROIDX_LIFECYCLE}"
    const val ANDROIDX_ORCHESTRATOR = "androidx.test:orchestrator:${Version.ANDROIDX_ORCHESTRATOR}"
    const val ANDROIDX_TEST_JUNIT = "androidx.test.ext:junit-ktx:${Version.ANDROIDX_TEST_JUNIT}"
    const val ANDROIDX_TEST_RUNNER = "androidx.test:runner:${Version.ANDROIDX_TEST_RUNNER}"
    const val ANDROIDX_TEST_RULES = "androidx.test:rules:${Version.ANDROIDX_TEST_RULES}"
    const val ANDROIDX_VECTOR_DRAWABLE = "androidx.vectordrawable:vectordrawable:${Version.ANDROIDX_VECTOR_DRAWABLE}"
    const val ARROW_CORE = "io.arrow-kt:arrow-core:${Version.ARROW}"
    const val ARROW_FX_COROUTINES = "io.arrow-kt:arrow-fx-coroutines:${Version.ARROW}"
    const val ARROW_META = "io.arrow-kt:arrow-meta:${Version.ARROW}"
    const val ARROW_OPTICS = "io.arrow-kt:arrow-optics:${Version.ARROW}"
    const val COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.COROUTINES}"
    const val COROUTINES_ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Version.COROUTINES}"
    const val COROUTINES_TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Version.COROUTINES}"
    const val GOOGLE_ANDROID_MATERIAL = "com.google.android.material:material:${Version.GOOGLE_ANDROID_MATERIAL}"
    const val JUNIT = "junit:junit:${Version.JUNIT}"
    const val JUNIT_VINTAGE_ENGINE = "org.junit.vintage:junit-vintage-engine:${Version.JUNIT_JUPITER}"
    const val JUNIT_JUPITER_API = "org.junit.jupiter:junit-jupiter-api:${Version.JUNIT_JUPITER}"
    const val JUNIT_JUPITER_ENGINE = "org.junit.jupiter:junit-jupiter-engine:${Version.JUNIT_JUPITER}"
    const val JUNIT_JUPITER_PARAMS = "org.junit.jupiter:junit-jupiter-params:${Version.JUNIT_JUPITER}"
    const val KOIN_ANDROID = "io.insert-koin:koin-android:${Version.KOIN}"
    const val KOIN_ANDROID_COMPOSE = "io.insert-koin:koin-androidx-compose:${Version.KOIN}"
    const val KOIN_TEST = "io.insert-koin:koin-test:${Version.KOIN}"
    const val MOCKITO = "org.mockito:mockito-junit-jupiter:${Version.MOCKITO}"
    const val MOCKITO_INLINE = "org.mockito:mockito-inline:${Version.MOCKITO}"
    const val MOCKITO_KOTLIN = "org.mockito.kotlin:mockito-kotlin:${Version.MOCKITO}"
    const val MOSHI = "com.squareup.moshi:moshi-kotlin:${Version.MOSHI}"
    const val MOSHI_ADAPTER = "com.squareup.moshi:moshi-adapters:${Version.MOSHI}"
    const val MOSHI_KOTLIN_CODEGEN = "com.squareup.moshi:moshi-kotlin-codegen:${Version.MOSHI}"
    const val LEAK_CANARY = "com.squareup.leakcanary:leakcanary-android:${Version.LEAK_CANARY}"
    const val PERMISSION_DISPATCHER = "com.github.permissions-dispatcher:permissionsdispatcher:${Version.PERMISSION_DISPATCHER}"
    const val PERMISSION_DISPATCHER_PROCESSOR = "com.github.permissions-dispatcher:permissionsdispatcher-processor:${Version.PERMISSION_DISPATCHER}"
    const val TURBINE = "app.cash.turbine:turbine:${Version.TURBINE}"
    const val TIMBER = "com.jakewharton.timber:timber:${Version.TIMBER}"
}
