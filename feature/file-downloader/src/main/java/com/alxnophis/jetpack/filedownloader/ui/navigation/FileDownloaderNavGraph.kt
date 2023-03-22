package com.alxnophis.jetpack.filedownloader.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.alxnophis.jetpack.filedownloader.di.injectFileDownloader
import com.alxnophis.jetpack.filedownloader.ui.view.FileDownloaderScreen
import com.alxnophis.jetpack.router.screen.FILE_DOWNLOADER
import com.alxnophis.jetpack.router.screen.Screen
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.fileDownloaderNavGraph(
    navController: NavController
) {
    navigation(
        startDestination = Screen.FileDownloader.route,
        route = FILE_DOWNLOADER
    ) {
        composable(
            route = Screen.FileDownloader.route
        ) {
            injectFileDownloader()
            FileDownloaderScreen(
                viewModel = getViewModel(),
                popBackStack = { navController.popBackStack() }
            )
        }
    }
}
