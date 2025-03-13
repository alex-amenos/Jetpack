package com.alxnophis.jetpack.root.ui.navigation

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.alxnophis.jetpack.authentication.ui.composable.AuthenticationFeature
import com.alxnophis.jetpack.authentication.ui.composable.AuthorizedFeature
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.filedownloader.ui.composable.FileDownloaderFeature
import com.alxnophis.jetpack.game.ballclicker.ui.composable.BallClickerFeature
import com.alxnophis.jetpack.home.domain.model.Feature
import com.alxnophis.jetpack.home.ui.composable.HomeFeature
import com.alxnophis.jetpack.location.tracker.ui.composable.LocationTrackerFeature
import com.alxnophis.jetpack.myplayground.ui.composable.MyPlaygroundFeature
import com.alxnophis.jetpack.notifications.ui.navigation.NotificationsFeature
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.ui.composable.PostDetailFeature
import com.alxnophis.jetpack.posts.ui.composable.PostsFeature
import com.alxnophis.jetpack.settings.ui.navigation.SettingsFeature
import kotlinx.coroutines.launch

@SuppressLint("ComposeModifierMissing")
@Composable
fun SetupNavGraph(navHostController: NavHostController) {
    AppTheme {
        NavHost(
            navController = navHostController,
            startDestination = Route.Home,
        ) {
            home(navHostController)
            authentication(navHostController)
            ballClicker(navHostController)
            fileDownloader(navHostController)
            myPlayground(navHostController)
            notifications(navHostController)
            locationTracker(navHostController)
            posts(navHostController)
            settings(navHostController)
        }
    }
}

private fun NavGraphBuilder.home(navHostController: NavHostController) {
    composable<Route.Home> {
        HomeFeature(
            onNavigateTo = { feature: Feature ->
                val route: Route =
                    when (feature) {
                        Feature.Authentication -> Route.Authentication
                        Feature.FileDownloader -> Route.FileDownloader
                        Feature.GameBallClicker -> Route.GameBallClicker
                        Feature.LocationTracker -> Route.LocationTracker
                        Feature.MyPlayground -> Route.MyPlayground
                        Feature.Notifications -> Route.Notifications
                        Feature.Posts -> Route.Posts
                        Feature.Settings -> Route.Settings
                    }
                navHostController.navigate(route)
            },
            onBack = { navHostController.popBackStack() },
        )
    }
}

private fun NavGraphBuilder.authentication(navHostController: NavHostController) {
    composable<Route.Authentication> {
        AuthenticationFeature(
            navigateInCaseOfSuccess = { email ->
                navHostController.navigate(
                    route = Route.Authorized(email),
                ) {
                    popUpTo(Route.Authentication) {
                        inclusive = true
                    }
                }
            },
            onBack = { navHostController.popBackStack() },
        )
    }
    composable<Route.Authorized> {
        val args = it.toRoute<Route.Authorized>()
        requireNotNull(args.email) { "Email can not be null, is required to login" }
        AuthorizedFeature(
            userEmail = args.email,
            onBack = { navHostController.popBackStack() },
        )
    }
}

private fun NavGraphBuilder.ballClicker(navHostController: NavHostController) {
    composable<Route.GameBallClicker> {
        BallClickerFeature(
            onBack = { navHostController.popBackStack() },
        )
    }
}

private fun NavGraphBuilder.fileDownloader(navHostController: NavHostController) {
    composable<Route.FileDownloader> {
        FileDownloaderFeature(onBack = { navHostController.popBackStack() })
    }
}

private fun NavGraphBuilder.myPlayground(navHostController: NavHostController) {
    composable<Route.MyPlayground> {
        MyPlaygroundFeature(onBack = { navHostController.popBackStack() })
    }
}

private fun NavGraphBuilder.notifications(navHostController: NavHostController) {
    composable<Route.Notifications> {
        NotificationsFeature(onBack = { navHostController.popBackStack() })
    }
}

private fun NavGraphBuilder.locationTracker(navHostController: NavHostController) {
    composable<Route.LocationTracker> {
        LocationTrackerFeature(onBack = { navHostController.popBackStack() })
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun NavGraphBuilder.posts(navHostController: NavHostController) {
    composable<Route.Posts> {
        val coroutineScope = rememberCoroutineScope()
        val navigator = rememberListDetailPaneScaffoldNavigator<Post>()
        val navigateBack: () -> Unit = {
            coroutineScope.launch {
                navigator.navigateBack()
            }
        }
        BackHandler(navigator.canNavigateBack()) {
            coroutineScope.launch {
                navigateBack()
            }
        }
        ListDetailPaneScaffold(
            directive = navigator.scaffoldDirective,
            value = navigator.scaffoldValue,
            listPane = {
                AnimatedPane {
                    PostsFeature(
                        onPostSelected = { post ->
                            coroutineScope.launch {
                                navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, post)
                            }
                        },
                        onBack = { navHostController.popBackStack() },
                    )
                }
            },
            detailPane = {
                AnimatedPane {
                    navigator.currentDestination?.contentKey?.let { post ->
                        PostDetailFeature(
                            post = post,
                            onBack = { navigateBack() },
                        )
                    }
                }
            },
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
        )
    }
}

private fun NavGraphBuilder.settings(navHostController: NavHostController) {
    composable<Route.Settings> {
        SettingsFeature(onBack = { navHostController.popBackStack() })
    }
}
