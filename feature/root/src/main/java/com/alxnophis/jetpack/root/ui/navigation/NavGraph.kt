package com.alxnophis.jetpack.root.ui.navigation

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.alxnophis.jetpack.authentication.ui.view.AuthenticationFeature
import com.alxnophis.jetpack.authentication.ui.view.AuthorizedFeature
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.filedownloader.ui.view.FileDownloaderFeature
import com.alxnophis.jetpack.game.ballclicker.ui.view.BallClickerFeature
import com.alxnophis.jetpack.home.ui.view.HomeFeature
import com.alxnophis.jetpack.location.tracker.ui.view.LocationTrackerFeature
import com.alxnophis.jetpack.myplayground.ui.view.MyPlaygroundFeature
import com.alxnophis.jetpack.notifications.ui.navigation.NotificationsFeature
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.ui.view.PostDetailFeature
import com.alxnophis.jetpack.posts.ui.view.PostsFeature
import com.alxnophis.jetpack.router.screen.Route
import com.alxnophis.jetpack.settings.ui.navigation.SettingsFeature

@SuppressLint("ComposeModifierMissing")
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SetupNavGraph(navHostController: NavHostController) {
    AppTheme {
        SetStatusBarColor()
        NavHost(
            navController = navHostController,
            startDestination = Route.Home,
        ) {
            // HOME module
            composable<Route.Home> {
                HomeFeature(onNavigateTo = { route: Route -> navHostController.navigate(route) }, onBack = { navHostController.popBackStack() })
            }
            // AUTHENTICATION Module
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
            // BALL CLICKER module
            composable<Route.GameBallClicker> {
                BallClickerFeature(
                    onBack = { navHostController.popBackStack() },
                )
            }
            // FILE DOWNLOADER module
            composable<Route.FileDownloader> {
                FileDownloaderFeature(onBack = { navHostController.popBackStack() })
            }
            // MY PLAYGROUND module
            composable<Route.MyPlayground> {
                MyPlaygroundFeature(onBack = { navHostController.popBackStack() })
            }
            // NOTIFICATIONS module
            composable<Route.Notifications> {
                NotificationsFeature(onBack = { navHostController.popBackStack() })
            }
            // LOCATION TRACKER module
            composable<Route.LocationTracker> {
                LocationTrackerFeature(onBack = { navHostController.popBackStack() })
            }
            // POSTS module
            composable<Route.Posts> {
                val navigator = rememberListDetailPaneScaffoldNavigator<Post>()
                BackHandler(navigator.canNavigateBack()) {
                    navigator.navigateBack()
                }
                ListDetailPaneScaffold(
                    directive = navigator.scaffoldDirective,
                    value = navigator.scaffoldValue,
                    listPane = {
                        AnimatedPane {
                            PostsFeature(
                                onPostSelected = { post ->
                                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, post)
                                },
                                onBack = { navHostController.popBackStack() },
                            )
                        }
                    },
                    detailPane = {
                        AnimatedPane {
                            navigator.currentDestination?.content?.let { post ->
                                PostDetailFeature(
                                    post = post,
                                    onBack = { navigator.navigateBack() },
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.systemBars),
                )
            }
            // SETTINGS module
            composable<Route.Settings> {
                SettingsFeature(onBack = { navHostController.popBackStack() })
            }
        }
    }
}

@Composable
internal fun SetStatusBarColor() {
    val activity = LocalView.current.context as Activity
    val colorArgb = MaterialTheme.colorScheme.primary.toArgb()
    activity.window.statusBarColor = colorArgb
}
