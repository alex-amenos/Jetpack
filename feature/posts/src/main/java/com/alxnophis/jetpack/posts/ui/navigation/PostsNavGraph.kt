package com.alxnophis.jetpack.posts.ui.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.alxnophis.jetpack.posts.di.injectPosts
import com.alxnophis.jetpack.posts.ui.contract.PostsEvent
import com.alxnophis.jetpack.posts.ui.view.PostsScreen
import com.alxnophis.jetpack.posts.ui.viewmodel.PostsViewModel
import com.alxnophis.jetpack.router.screen.Route
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.postsNavGraph(navController: NavHostController) {
    composable<Route.Posts> {
        injectPosts()
        val viewModel = getViewModel<PostsViewModel>()
        PostsScreen(
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            onEvent = { event ->
                when (event) {
                    PostsEvent.GoBackRequested -> navController.popBackStack()
                    else -> viewModel.handleEvent(event)
                }
            },
        )
    }
}
