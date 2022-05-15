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
                authentication,
                settings,
                posts,
                locationTracker,
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
    }
}
