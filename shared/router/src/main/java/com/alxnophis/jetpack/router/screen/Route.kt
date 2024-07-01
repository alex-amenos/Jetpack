package com.alxnophis.jetpack.router.screen

import kotlinx.serialization.Serializable


@Serializable
sealed class Route {
    @Serializable
    data object Authentication : Route()

    @Serializable
    data class Authorized(val email: String) : Route()

    @Serializable
    data object Home : Route()

    @Serializable
    data object FileDownloader : Route()

    @Serializable
    data object GameBallClicker : Route()

    @Serializable
    data object MyPlayground : Route()

    @Serializable
    data object Notifications : Route()

    @Serializable
    data object LocationTracker : Route()

    @Serializable
    data object Posts : Route()

    @Serializable
    data object Settings : Route()

    @Serializable
    data object Spacex : Route()
}
