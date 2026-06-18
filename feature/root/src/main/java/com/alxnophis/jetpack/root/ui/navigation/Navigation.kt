package com.alxnophis.jetpack.root.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.alxnophis.jetpack.authentication.ui.composable.AuthenticationFeature
import com.alxnophis.jetpack.authentication.ui.composable.AuthorizedFeature
import com.alxnophis.jetpack.filedownloader.ui.composable.FileDownloaderFeature
import com.alxnophis.jetpack.game.ballclicker.ui.composable.BallClickerFeature
import com.alxnophis.jetpack.home.domain.model.Feature
import com.alxnophis.jetpack.home.ui.composable.HomeFeature
import com.alxnophis.jetpack.location.tracker.ui.composable.LocationTrackerFeature
import com.alxnophis.jetpack.movies.ui.composable.MovieDetailFeature
import com.alxnophis.jetpack.movies.ui.composable.MovieNotSelectedComposable
import com.alxnophis.jetpack.movies.ui.composable.MoviesFeature
import com.alxnophis.jetpack.myplayground.ui.composable.MyPlaygroundFeature
import com.alxnophis.jetpack.notifications.ui.navigation.NotificationsFeature
import com.alxnophis.jetpack.posts.ui.composable.PostDetailFeature
import com.alxnophis.jetpack.posts.ui.composable.PostNotSelectedComposable
import com.alxnophis.jetpack.posts.ui.composable.PostsFeature
import com.alxnophis.jetpack.settings.ui.navigation.SettingsFeature

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@SuppressLint("ComposeModifierMissing")
@Composable
fun Navigation(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(Route.Home)
    val onBack: () -> Unit = { backStack.removeLastOrNull() }
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val customDirective = calculatePaneScaffoldDirective(adaptiveInfo).copy(horizontalPartitionSpacerSize = 0.dp)
    val listDetailStrategy = rememberListDetailSceneStrategy<Any>(directive = customDirective)

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = onBack,
        sceneStrategies = listOf(listDetailStrategy),
        entryDecorators =
            listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
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
                                    Feature.Movies -> Route.Movies
                                    Feature.MyPlayground -> Route.MyPlayground
                                    Feature.Notifications -> Route.Notifications
                                    Feature.Posts -> Route.Posts
                                    Feature.Settings -> Route.Settings
                                }
                            backStack.add(route)
                        },
                        onBack = onBack,
                    )
                }
                entry<Route.Authentication> {
                    AuthenticationFeature(
                        navigateInCaseOfSuccess = { email ->
                            backStack.add(Route.Authorized(email))
                        },
                        onBack = onBack,
                    )
                }
                entry<Route.Authorized> { key ->
                    AuthorizedFeature(
                        userEmail = key.email,
                        onBack = {
                            backStack.removeAll { it is Route.Authentication || it is Route.Authorized }
                        },
                    )
                }
                entry<Route.GameBallClicker> {
                    BallClickerFeature(
                        onBack = onBack,
                    )
                }
                entry<Route.FileDownloader> {
                    FileDownloaderFeature(
                        onBack = onBack,
                    )
                }
                entry<Route.MyPlayground> {
                    MyPlaygroundFeature(
                        onBack = onBack,
                    )
                }
                entry<Route.Notifications> {
                    NotificationsFeature(
                        onBack = onBack,
                    )
                }
                entry<Route.LocationTracker> {
                    LocationTrackerFeature(
                        onBack = onBack,
                    )
                }
                entry<Route.Movies>(
                    metadata =
                        ListDetailSceneStrategy.listPane {
                            MovieNotSelectedComposable(
                                modifier = Modifier.fillMaxSize(),
                            )
                        },
                ) {
                    MoviesFeature(
                        onMovieSelected = { movieId ->
                            if (backStack.last() is Route.MovieDetail) {
                                backStack.removeLastOrNull()
                            }
                            backStack.add(Route.MovieDetail(movieId))
                        },
                        onBack = onBack,
                    )
                }
                entry<Route.MovieDetail>(
                    metadata = ListDetailSceneStrategy.detailPane(),
                ) { key ->
                    MovieDetailFeature(
                        movieId = key.movieId,
                        onBack = onBack,
                    )
                }
                entry<Route.Settings> {
                    SettingsFeature(
                        onBack = onBack,
                    )
                }
                entry<Route.Posts>(
                    metadata =
                        ListDetailSceneStrategy.listPane {
                            PostNotSelectedComposable(
                                modifier = Modifier.fillMaxSize(),
                            )
                        },
                ) {
                    PostsFeature(
                        onPostSelected = { postId ->
                            if (backStack.last() is Route.PostDetail) {
                                backStack.removeLastOrNull()
                            }
                            backStack.add(Route.PostDetail(postId))
                        },
                        onBack = onBack,
                    )
                }
                entry<Route.PostDetail>(
                    metadata = ListDetailSceneStrategy.detailPane(),
                ) { key ->
                    PostDetailFeature(
                        postId = key.postId,
                        onBack = onBack,
                    )
                }
            },
    )
}
