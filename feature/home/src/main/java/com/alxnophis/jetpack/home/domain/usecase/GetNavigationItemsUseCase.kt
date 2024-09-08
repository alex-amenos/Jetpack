package com.alxnophis.jetpack.home.domain.usecase

import arrow.core.Either
import com.alxnophis.jetpack.home.domain.model.NavigationError
import com.alxnophis.jetpack.home.domain.model.NavigationItem
import com.alxnophis.jetpack.router.screen.Route
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetNavigationItemsUseCase(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(): Either<NavigationError, List<NavigationItem>> =
        withContext(ioDispatcher) {
            Either
                .catch {
                    listOf(
                        myPlayground,
                        fileDownloader,
                        authentication,
                        settings,
                        notifications,
                        posts,
                        locationTracker,
                        gameBallClicker,
                    )
                }.mapLeft { NavigationError.Unknown }
        }

    // TODO - Update Home navigation here
    companion object {
        private val authentication =
            NavigationItem(
                name = "Authentication",
                emoji = "🗝",
                description = "Authentication form with SignUp and SignIn",
                route = Route.Authentication
            )
        private val settings =
            NavigationItem(
                name = "Settings",
                emoji = "⚙️",
                description = "Settings options screen",
                route = Route.Settings
            )
        private val posts =
            NavigationItem(
                name = "Posts",
                emoji = "📄",
                description = "Load posts from JsonPlaceholder API",
                route = Route.Posts
            )
        private val locationTracker =
            NavigationItem(
                name = "Location Tracker",
                emoji = "📍",
                description = "User location tracking",
                route = Route.LocationTracker
            )
        private val gameBallClicker =
            NavigationItem(
                name = "Ball Clicker Game",
                emoji = "🔴",
                description = "Be the fastest ball clicker!",
                route = Route.GameBallClicker
            )
        private val notifications =
            NavigationItem(
                name = "Notifications",
                emoji = "🔔",
                description = "Push notifications",
                route = Route.Notifications
            )
        private val myPlayground =
            NavigationItem(
                name = "️MyPlayground",
                emoji = "⭐️",
                description = "My Jetpack playground module",
                route = Route.MyPlayground
            )
        private val fileDownloader =
            NavigationItem(
                name = "FileDownloader",
                emoji = "📥",
                description = "Download a file by URL to download folder",
                route = Route.FileDownloader
            )
    }
}
