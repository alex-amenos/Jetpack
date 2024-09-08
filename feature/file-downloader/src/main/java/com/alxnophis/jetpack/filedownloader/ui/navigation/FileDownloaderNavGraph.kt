package com.alxnophis.jetpack.filedownloader.ui.navigation

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alxnophis.jetpack.filedownloader.di.injectFileDownloader
import com.alxnophis.jetpack.filedownloader.ui.contract.FileDownloaderEvent
import com.alxnophis.jetpack.filedownloader.ui.view.FileDownloaderScreen
import com.alxnophis.jetpack.filedownloader.ui.viewmodel.FileDownloaderViewModel
import com.alxnophis.jetpack.router.screen.Route
import org.koin.androidx.compose.getViewModel

fun NavGraphBuilder.fileDownloaderNavGraph(navController: NavController) {
    composable<Route.FileDownloader> {
        injectFileDownloader()
        val viewModel = getViewModel<FileDownloaderViewModel>()
        FileDownloaderScreen(
            state = viewModel.uiState.collectAsStateWithLifecycle().value,
            onEvent = { event ->
                when (event) {
                    FileDownloaderEvent.GoBackRequested -> navController.popBackStack()
                    else -> viewModel.handleEvent(event)
                }
            },
        )
    }
}
