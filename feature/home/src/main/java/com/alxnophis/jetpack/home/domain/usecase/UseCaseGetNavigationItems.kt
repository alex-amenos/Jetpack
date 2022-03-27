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

    companion object {
        private val authentication = NavigationItem(
            name = "Authentication",
            intent = RouterAuthentication.dynamicStart
        )
        private val settings = NavigationItem(
            name = "Settings",
            intent = RouterSettings.dynamicStart
        )
    }
}
