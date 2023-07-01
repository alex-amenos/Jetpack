package com.alxnophis.jetpack.posts.ui.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.alxnophis.jetpack.posts.di.injectPosts
import com.alxnophis.jetpack.posts.ui.contract.PostsEvent
import com.alxnophis.jetpack.posts.ui.view.PostsScreen
import com.alxnophis.jetpack.posts.ui.viewmodel.PostsViewModel
import com.alxnophis.jetpack.router.screen.POSTS_ROUTE
import com.alxnophis.jetpack.router.screen.Screen
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.postsNavGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.Posts.route,
        route = POSTS_ROUTE
    ) {
        composable(
            route = Screen.Posts.route
        ) {
            injectPosts()
            val viewModel = getViewModel<PostsViewModel>()
            PostsScreen(
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                onEvent = { event ->
                    when (event) {
                        PostsEvent.GoBackRequested -> navController.popBackStack()
                        else -> viewModel.handleEvent(event)
                    }
                }
            )
        }
    }
}
