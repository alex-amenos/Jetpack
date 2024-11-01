package com.alxnophis.jetpack.home.domain.model

sealed interface Feature {
    data object Authentication : Feature
    data object FileDownloader : Feature
    data object GameBallClicker : Feature
    data object MyPlayground : Feature
    data object Notifications : Feature
    data object LocationTracker : Feature
    data object Posts : Feature
    data object Settings : Feature
}
