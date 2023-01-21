package com.alxnophis.jetpack.authentication.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.alxnophis.jetpack.authentication.di.injectAuthentication
import com.alxnophis.jetpack.authentication.ui.view.AuthenticationScreen
import com.alxnophis.jetpack.authentication.ui.view.AuthorizedScreen
import com.alxnophis.jetpack.router.screen.ARGUMENT_EMAIL
import com.alxnophis.jetpack.router.screen.AUTHENTICATION_ROUTE
import com.alxnophis.jetpack.router.screen.Screen
import com.alxnophis.jetpack.router.screen.replaceArgument
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
                navigateNextStep = { userEmail: String ->
                    navController.navigate(
                        route = Screen.Authorized.replaceArgument(ARGUMENT_EMAIL, userEmail)
                    ) {
                        // Remove Authentication screen form back stack
                        popUpTo(Screen.Authentication.route) {
                            inclusive = true
                        }
                    }
                },
                popBackStack = { navController.popBackStack() },
                viewModel = getViewModel()
            )
        }
        composable(
            route = Screen.Authorized.route,
            arguments = listOf(
                navArgument(ARGUMENT_EMAIL) { type = NavType.StringType }
            )
        ) {
            val email = it.arguments?.getString(ARGUMENT_EMAIL)
            requireNotNull(email) { "Email can not be null, is required to login" }
            AuthorizedScreen(
                popBackStack = { navController.popBackStack() },
                userEmail = email
            )
        }
    }
}
