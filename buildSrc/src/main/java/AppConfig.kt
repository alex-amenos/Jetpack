@file:Suppress("unused", "MemberVisibilityCanBePrivate", "SpellCheckingInspection")

object AppConfig {
    const val APPLICATION_ID = "com.alxnophis.jetpack"
    const val APP_PACKAGE_NAME = APPLICATION_ID
    const val APP_PACKAGE_NAME_DEBUG = "$APPLICATION_ID.debug"
    const val COMPILE_SDK_VERSION = 33
    const val BUILD_TOOLS_VERSION = "33.0.0"
    const val MIN_SDK_VERSION = 26
    const val TARGET_SDK_VERSION = 33
}

object Modules {
    // App modules
    const val APP = ":app"
    // Shared modules
    const val SHARED_API = ":shared:api"
    const val SHARED_CORE = ":shared:core"
    const val SHARED_KOTLIN = ":shared:kotlin"
    const val SHARED_ROUTER = ":shared:router"
    const val SHARED_TESTING = ":shared:testing"
    // Feature modules
    const val FEATURE_HOME = ":feature:home"
    const val FEATURE_AUTHENTICATION = ":feature:authentication"
    const val FEATURE_GAME_BALL_CLICKER = ":feature:game:ballclicker"
    const val FEATURE_MY_PLAYGROUND = ":feature:my-playground"
    const val FEATURE_NOTIFICATIONS = ":feature:notifications"
    const val FEATURE_LOCATION_TRACKER =":feature:location-tracker"
    const val FEATURE_POSTS = ":feature:posts"
    const val FEATURE_ROOT = ":feature:root"
    const val FEATURE_SETTINGS = ":feature:settings"
    const val FEATURE_SPACEX = ":feature:spacex"
}
