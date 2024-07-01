package com.alxnophis.jetpack.notifications.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alxnophis.jetpack.notifications.di.injectNotifications
import com.alxnophis.jetpack.notifications.ui.view.NotificationsScreen
import com.alxnophis.jetpack.router.screen.Route

fun NavGraphBuilder.notificationsNavGraph(navController: NavController) {
    composable<Route.Notifications> {
        injectNotifications()
        NotificationsScreen(
            navigateBack = { navController.popBackStack() },
        )
    }
}
