package com.alxnophis.jetpack.filedownloader.ui.composable

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.filedownloader.di.injectFileDownloader
import com.alxnophis.jetpack.filedownloader.ui.contract.FileDownloaderUiEvent
import com.alxnophis.jetpack.filedownloader.ui.viewmodel.FileDownloaderViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FileDownloaderFeature(onBack: () -> Unit) {
    injectFileDownloader()
    val viewModel = koinViewModel<FileDownloaderViewModel>()
    FileDownloaderScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            when (event) {
                FileDownloaderUiEvent.GoBackRequested -> onBack()
                else -> viewModel.handleEvent(event)
            }
        },
    )
}
