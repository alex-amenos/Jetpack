package com.alxnophis.jetpack.authentication.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.alxnophis.jetpack.authentication.di.injectAuthentication
import com.alxnophis.jetpack.authentication.ui.view.AuthenticationScreen
import com.alxnophis.jetpack.authentication.ui.view.AuthorizedScreen
import com.alxnophis.jetpack.core.base.constants.EMPTY
import com.alxnophis.jetpack.router.screen.AUTHENTICATION_ARGUMENT_EMAIL
import com.alxnophis.jetpack.router.screen.AUTHENTICATION_ROUTE
import com.alxnophis.jetpack.router.screen.Screen
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.authenticationNavGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.Authentication.route,
        route = AUTHENTICATION_ROUTE
    ) {
        composable(
            route = Screen.Authentication.route,
        ) {
            injectAuthentication()
            AuthenticationScreen(
                navController = navController,
                viewModel = getViewModel()
            )
        }
        composable(
            route = Screen.Authorized.route
        ) {
            AuthorizedScreen(
                navController = navController,
                authEmail = it.arguments?.getString(AUTHENTICATION_ARGUMENT_EMAIL) ?: EMPTY
            )
        }
    }
}
