package com.alxnophis.jetpack.authentication.ui.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.alxnophis.jetpack.authentication.di.injectAuthentication
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEvent
import com.alxnophis.jetpack.authentication.ui.view.AuthenticationScreen
import com.alxnophis.jetpack.authentication.ui.view.AuthorizedScreen
import com.alxnophis.jetpack.authentication.ui.viewmodel.AuthenticationViewModel
import com.alxnophis.jetpack.router.screen.Route
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.authenticationNavGraph(navController: NavHostController) {
    composable<Route.Authentication> {
        injectAuthentication()
        val viewModel = getViewModel<AuthenticationViewModel>()
        AuthenticationScreen(
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            onEvent = { event ->
                when (event) {
                    AuthenticationEvent.GoBackRequested -> navController.popBackStack()
                    is AuthenticationEvent.NavigateToAuthScreenRequested -> {
                        navController.navigate(
                            route = Route.Authorized(event.email),
                        ) {
                            popUpTo(Route.Authentication) {
                                inclusive = true
                            }
                        }
                    }
                    else -> viewModel.handleEvent(event)
                }
            },
        )
    }
    composable<Route.Authorized> {
        val args = it.toRoute<Route.Authorized>()
        requireNotNull(args.email) { "Email can not be null, is required to login" }
        AuthorizedScreen(
            userEmail = args.email,
            navigateBack = { navController.popBackStack() },
        )
    }
}
