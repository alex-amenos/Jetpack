package com.alxnophis.jetpack.home.domain.usecase

import arrow.core.Either
import com.alxnophis.jetpack.home.domain.model.NavigationError
import com.alxnophis.jetpack.home.domain.model.NavigationItem
import com.alxnophis.jetpack.router.screen.Screen
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UseCaseGetNavigationItems(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend operator fun invoke(): Either<NavigationError, List<NavigationItem>> = withContext(ioDispatcher) {
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
                    spacex
                )
            }
            .mapLeft { NavigationError.Unknown }
    }

    // TODO - Update Home navigation here
    companion object {
        private val authentication = NavigationItem(
            name = "Authentication",
            emoji = "🗝",
            description = "Authentication form with SignUp and SignIn",
            screen = Screen.Authentication
        )
        private val settings = NavigationItem(
            name = "Settings",
            emoji = "⚙️",
            description = "Settings options screen",
            screen = Screen.Settings
        )
        private val posts = NavigationItem(
            name = "Posts",
            emoji = "📄",
            description = "Load posts from JsonPlaceholder API",
            screen = Screen.Posts
        )
        private val locationTracker = NavigationItem(
            name = "Location Tracker",
            emoji = "📍",
            description = "User location tracking",
            screen = Screen.LocationTracker
        )
        private val gameBallClicker = NavigationItem(
            name = "Ball Clicker Game",
            emoji = "🔴",
            description = "Be the fastest ball clicker!",
            screen = Screen.GameBallClicker
        )
        private val spacex = NavigationItem(
            name = "SpaceX",
            emoji = "🚀",
            description = "Welcome to the SpaceX GraphQL API",
            screen = Screen.Spacex
        )
        private val notifications = NavigationItem(
            name = "Notifications",
            emoji = "🔔",
            description = "Push notifications",
            screen = Screen.Notifications
        )
        private val myPlayground = NavigationItem(
            name = "️MyPlayground",
            emoji = "⭐️",
            description = "My Jetpack playground module",
            screen = Screen.MyPlayground
        )
        private val fileDownloader = NavigationItem(
            name = "FileDownloader",
            emoji = "📥",
            description = "Download a file by URL to download folder",
            screen = Screen.FileDownloader
        )
    }
}
