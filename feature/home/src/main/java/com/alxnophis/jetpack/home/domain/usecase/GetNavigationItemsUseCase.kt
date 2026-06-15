package com.alxnophis.jetpack.home.domain.usecase

import arrow.core.Either
import com.alxnophis.jetpack.home.domain.model.Feature
import com.alxnophis.jetpack.home.domain.model.NavigationError
import com.alxnophis.jetpack.home.domain.model.NavigationItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetNavigationItemsUseCase(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend operator fun invoke(): Either<NavigationError, List<NavigationItem>> =
        withContext(ioDispatcher) {
            Either
                .catch {
                    listOf(
                        myPlayground,
                        movies,
                        posts,
                        fileDownloader,
                        authentication,
                        settings,
                        notifications,
                        locationTracker,
                        gameBallClicker,
                    )
                }.mapLeft { NavigationError.Unknown }
        }

    // TODO - Update Home navigation here with new features and remove the ones that are not needed anymore
    companion object {
        private val authentication =
            NavigationItem(
                name = "Authentication",
                emoji = "🗝",
                description = "Authentication form with SignUp and SignIn",
                feature = Feature.Authentication,
            )
        private val settings =
            NavigationItem(
                name = "Settings",
                emoji = "⚙️",
                description = "Settings options screen",
                feature = Feature.Settings,
            )
        private val posts =
            NavigationItem(
                name = "Posts",
                emoji = "📄",
                description = "Load posts from JsonPlaceholder API",
                feature = Feature.Posts,
            )
        private val locationTracker =
            NavigationItem(
                name = "Location Tracker",
                emoji = "📍",
                description = "User location tracking",
                feature = Feature.LocationTracker,
            )
        private val gameBallClicker =
            NavigationItem(
                name = "Ball Clicker Game",
                emoji = "🔴",
                description = "Be the fastest ball clicker!",
                feature = Feature.GameBallClicker,
            )
        private val notifications =
            NavigationItem(
                name = "Notifications",
                emoji = "🔔",
                description = "Push notifications",
                feature = Feature.Notifications,
            )
        private val myPlayground =
            NavigationItem(
                name = "️MyPlayground",
                emoji = "⭐️",
                description = "My Jetpack playground module",
                feature = Feature.MyPlayground,
            )
        private val fileDownloader =
            NavigationItem(
                name = "FileDownloader",
                emoji = "📥",
                description = "Download a file by URL to download folder",
                feature = Feature.FileDownloader,
            )
        private val movies =
            NavigationItem(
                name = "Movies",
                emoji = "🎬",
                description = "Movies feature",
                feature = Feature.Movies,
            )
    }
}
