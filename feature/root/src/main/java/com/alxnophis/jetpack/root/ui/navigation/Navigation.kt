package com.alxnophis.jetpack.root.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
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
import com.alxnophis.jetpack.posts.ui.composable.PostDetailFeature
import com.alxnophis.jetpack.posts.ui.composable.PostsFeature
import com.alxnophis.jetpack.settings.ui.navigation.SettingsFeature

@SuppressLint("ComposeModifierMissing")
@Composable
fun Navigation(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        val backStack = rememberNavBackStack(Route.Home)
        NavDisplay(
            backStack = backStack,
            modifier = modifier,
            entryDecorators =
                listOf(
                    rememberSavedStateNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                    rememberSceneSetupNavEntryDecorator(),
                ),
            entryProvider =
                entryProvider {
                    entry<Route.Home> {
                        HomeFeature(
                            onNavigateTo = { feature: Feature ->
                                val route: NavKey =
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
                                backStack.add(route)
                            },
                            onBack = { backStack.removeLastOrNull() },
                        )
                    }
                    entry<Route.Authentication> {
                        AuthenticationFeature(
                            navigateInCaseOfSuccess = { email ->
                                backStack.add(Route.Authorized(email))
                            },
                            onBack = { backStack.removeLastOrNull() },
                        )
                    }
                    entry<Route.Authorized> { key ->
                        AuthorizedFeature(
                            userEmail = key.email,
                            onBack = { backStack.removeLastOrNull() },
                        )
                    }
                    entry<Route.GameBallClicker> {
                        BallClickerFeature(
                            onBack = { backStack.removeLastOrNull() },
                        )
                    }
                    entry<Route.FileDownloader> {
                        FileDownloaderFeature(
                            onBack = { backStack.removeLastOrNull() },
                        )
                    }
                    entry<Route.MyPlayground> {
                        MyPlaygroundFeature(
                            onBack = { backStack.removeLastOrNull() },
                        )
                    }
                    entry<Route.Notifications> {
                        NotificationsFeature(
                            onBack = { backStack.removeLastOrNull() },
                        )
                    }
                    entry<Route.LocationTracker> {
                        LocationTrackerFeature(
                            onBack = { backStack.removeLastOrNull() },
                        )
                    }
                    entry<Route.Settings> {
                        SettingsFeature(
                            onBack = { backStack.removeLastOrNull() },
                        )
                    }
                    entry<Route.Posts> {
                        PostsFeature(
                            onPostSelected = { postId ->
                                backStack.add(Route.PostDetail(postId))
                            },
                            onBack = { backStack.removeLastOrNull() },
                        )
                    }
                    entry<Route.PostDetail> { key ->
                        PostDetailFeature(
                            postId = key.postId,
                            onBack = { backStack.removeLastOrNull() },
                        )
                    }
                },
        )
    }
}
