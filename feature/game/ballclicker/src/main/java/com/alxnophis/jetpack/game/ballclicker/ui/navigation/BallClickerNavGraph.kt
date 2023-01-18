package com.alxnophis.jetpack.game.ballclicker.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.alxnophis.jetpack.game.ballclicker.di.injectBallClicker
import com.alxnophis.jetpack.game.ballclicker.ui.view.BallClickerScreen
import com.alxnophis.jetpack.router.screen.GAME_BALL_CLICKER_ROUTE
import com.alxnophis.jetpack.router.screen.Screen

fun NavGraphBuilder.ballClickerNavGraph(
    navController: NavController
) {
    navigation(
        startDestination = Screen.GameBallClicker.route,
        route = GAME_BALL_CLICKER_ROUTE
    ) {
        composable(
            route = Screen.GameBallClicker.route
        ) {
            injectBallClicker()
            BallClickerScreen(
                popBackStack = { navController.popBackStack() },
            )
        }
    }
}
