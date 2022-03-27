package com.alxnophis.jetpack.home.domain.usecase

import arrow.core.Either
import com.alxnophis.jetpack.home.domain.model.NavigationError
import com.alxnophis.jetpack.home.domain.model.NavigationItem
import com.alxnophis.jetpack.router.features.RouterAuthentication
import com.alxnophis.jetpack.router.features.RouterSettings

class UseCaseGetNavigationItems {

    operator fun invoke(): Either<NavigationError, List<NavigationItem>> = Either.catch(
        { NavigationError.Unknown },
        {
            listOf(
                authentication,
                settings
            )
        }
    )

    // TODO - Update Home navigation here
    companion object {
        private val authentication = NavigationItem(
            name = "Authentication",
            description = "Authentication form with SignUp and SignIn",
            intent = RouterAuthentication.dynamicStart
        )
        private val settings = NavigationItem(
            name = "Settings",
            description = "Settings options screen",
            intent = RouterSettings.dynamicStart
        )
    }
}
