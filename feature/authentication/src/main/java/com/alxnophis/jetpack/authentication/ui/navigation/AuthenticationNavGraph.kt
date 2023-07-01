package com.alxnophis.jetpack.authentication.ui.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.alxnophis.jetpack.authentication.di.injectAuthentication
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEvent
import com.alxnophis.jetpack.authentication.ui.view.AuthenticationScreen
import com.alxnophis.jetpack.authentication.ui.view.AuthorizedScreen
import com.alxnophis.jetpack.authentication.ui.viewmodel.AuthenticationViewModel
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
            route = Screen.Authentication.route
        ) {
            injectAuthentication()
            val viewModel = getViewModel<AuthenticationViewModel>()
            AuthenticationScreen(
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                onEvent = { event ->
                    when (event) {
                        AuthenticationEvent.GoBackRequested -> navController.popBackStack()
                        is AuthenticationEvent.NavigateToAuthScreenRequested -> {
                            navController.navigate(
                                route = Screen.Authorized.route.replaceArgument(ARGUMENT_EMAIL, event.email)
                            ) {
                                // Remove Authentication screen form back stack
                                popUpTo(Screen.Authentication.route) {
                                    inclusive = true
                                }
                            }
                        }
                        else -> viewModel.handleEvent(event)
                    }
                }
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
                userEmail = email,
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}
