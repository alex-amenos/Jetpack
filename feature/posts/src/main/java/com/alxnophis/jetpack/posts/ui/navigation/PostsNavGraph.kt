package com.alxnophis.jetpack.posts.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.di.injectPosts
import com.alxnophis.jetpack.posts.ui.contract.PostsEvent
import com.alxnophis.jetpack.posts.ui.view.PostDetail
import com.alxnophis.jetpack.posts.ui.view.PostsScreen
import com.alxnophis.jetpack.posts.ui.viewmodel.PostsViewModel
import com.alxnophis.jetpack.router.screen.Route
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun NavGraphBuilder.postsNavGraph(navController: NavHostController) {
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
                    injectPosts()
                    val viewModel = getViewModel<PostsViewModel>()
                    PostsScreen(
                        state = viewModel.uiState.collectAsStateWithLifecycle().value,
                        onEvent = { event ->
                            when (event) {
                                PostsEvent.GoBackRequested -> navController.popBackStack()
                                is PostsEvent.OnPostClicked -> navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, event.post)
                                else -> viewModel.handleEvent(event)
                            }
                        },
                    )
                }
            },
            detailPane = {
                AnimatedPane {
                    navigator.currentDestination?.content?.let { post ->
                        PostDetail(
                            onNavigateBack = { navigator.navigateBack() },
                            post = post,
                        )
                    }
                }
            },
        )
    }
}
