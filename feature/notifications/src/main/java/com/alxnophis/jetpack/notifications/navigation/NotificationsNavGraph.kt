package com.alxnophis.jetpack.notifications.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.alxnophis.jetpack.notifications.di.injectNotifications
import com.alxnophis.jetpack.notifications.ui.view.NotificationsScreen
import com.alxnophis.jetpack.router.screen.NOTIFICATIONS_ROUTE
import com.alxnophis.jetpack.router.screen.Screen

fun NavGraphBuilder.notificationsNavGraph(
    navController: NavController
) {
    navigation(
        startDestination = Screen.Notifications.route,
        route = NOTIFICATIONS_ROUTE
    ) {
        composable(
            route = Screen.Notifications.route
        ) {
            injectNotifications()
            NotificationsScreen(
                popBackStack = { navController.popBackStack() }
            )
        }
    }
}
