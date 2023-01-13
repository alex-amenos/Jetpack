package com.alxnophis.jetpack.home.domain.usecase

import arrow.core.Either
import com.alxnophis.jetpack.home.domain.model.NavigationError
import com.alxnophis.jetpack.home.domain.model.NavigationItem
import com.alxnophis.jetpack.router.screen.Screen

class UseCaseGetNavigationItems {

    operator fun invoke(): Either<NavigationError, List<NavigationItem>> = Either.catch(
        { NavigationError.Unknown },
        {
            listOf(
                myPlayground,
                authentication,
                settings,
                notifications,
                posts,
                locationTracker,
                gameBallClicker,
                spacex,
            )
        }
    )

    // TODO - Update Home navigation here
    companion object {
        private val authentication = NavigationItem(
            name = "Authentication",
            description = "Authentication form with SignUp and SignIn",
            screen = Screen.Authentication,
        )
        private val settings = NavigationItem(
            name = "Settings",
            description = "Settings options screen",
            screen = Screen.Settings,
        )
        private val posts = NavigationItem(
            name = "Posts",
            description = "Load posts from JsonPlaceholder API",
            screen = Screen.Posts,
        )
        private val locationTracker = NavigationItem(
            name = "Location Tracker",
            description = "User location tracking",
            screen = Screen.LocationTracker,
        )
        private val gameBallClicker = NavigationItem(
            name = "Ball Clicker Game",
            description = "Be the fastest ball clicker!",
            screen = Screen.GameBallClicker
        )
        private val spacex = NavigationItem(
            name = "SpaceX",
            description = "Welcome to the SpaceX GraphQL API",
            screen = Screen.Spacex
        )
        private val notifications = NavigationItem(
            name = "Notifications",
            description = "Push notifications",
            screen = Screen.Notifications
        )
        private val myPlayground = NavigationItem(
            name = "MyPlayground",
            description = "My Jetpack Compose playground",
            screen = Screen.MyPlayground
        )
    }
}
