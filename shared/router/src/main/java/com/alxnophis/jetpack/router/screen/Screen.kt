package com.alxnophis.jetpack.router.screen

// Routes
const val ROOT_ROUTE = "root"
const val AUTHENTICATION_ROUTE = "authentication"
const val GAME_BALL_CLICKER_ROUTE = "game-ball-clicker"
const val HOME_ROUTE = "home"
const val LOCATION_TRACKER_ROUTE = "location-tracker"
const val POSTS_ROUTE = "posts"
const val SETTINGS_ROUTE = "settings"
const val SPACEX_ROUTE = "spacex"

// Arguments
const val AUTHENTICATION_ARGUMENT_EMAIL = "email"

// Screens
sealed class Screen(val route: String) {
    object Authentication : Screen(route = "authentication_screen")
    object Authorized : Screen(route = "authorized_screen?email={$AUTHENTICATION_ARGUMENT_EMAIL}") {
        fun routeWithParams(email: String) = route.replaceArgument(AUTHENTICATION_ARGUMENT_EMAIL, email)
    }
    object GameBallClicker : Screen(route = "game_ball_clicker_screen")
    object Home : Screen(route = "home_screen")
    object LocationTracker : Screen(route = "location_tracker_screen")
    object Posts : Screen(route = "posts_screen")
    object Settings : Screen(route = "settings_screen")
    object Spacex : Screen(route = "spacex_screen")
}

@Suppress("unused")
private fun String.replaceArgument(oldValue: String, newValue: String) = this.replace(
    oldValue = "{$oldValue}",
    newValue = newValue
)
