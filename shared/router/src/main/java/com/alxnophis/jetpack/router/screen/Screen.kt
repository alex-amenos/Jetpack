package com.alxnophis.jetpack.router.screen

// Routes
const val ROOT_ROUTE = "root"
const val AUTHENTICATION_ROUTE = "authentication"
const val GAME_BALL_CLICKER_ROUTE = "game-ball-clicker"
const val HOME_ROUTE = "home"
const val MUSIC_DASHBOARD = "music_dashboard"
const val LOCATION_TRACKER_ROUTE = "location-tracker"
const val POSTS_ROUTE = "posts"
const val SETTINGS_ROUTE = "settings"

// Screens
sealed class Screen(val route: String) {
    object Authentication : Screen(route = "authentication_screen")
    object Authorized : Screen(route = "authorized_screen")
    object GameBallClicker : Screen(route = "game_ball_clicker_screen")
    object Home : Screen(route = "home_screen")
    object MusicDashboard : Screen(route = "music_dashboard_screen")
    object LocationTracker : Screen(route = "location_tracker_screen")
    object Posts : Screen(route = "posts_screen")
    object Settings : Screen(route = "settings_screen")
}

@Suppress("unused")
private fun String.replaceMandatoryArgument(oldValue: String, newValue: String) = this.replace(
    oldValue = "{$oldValue}",
    newValue = newValue
)
