package com.alxnophis.jetpack.filedownloader.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.filedownloader.ui.contract.FileDownloaderUiEvent
import com.alxnophis.jetpack.filedownloader.ui.viewmodel.FileDownloaderViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FileDownloaderFeature(onBack: () -> Unit) {
    val viewModel = koinViewModel<FileDownloaderViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FileDownloaderScreen(
        uiState = uiState,
        onEvent = { event ->
            when (event) {
                FileDownloaderUiEvent.GoBackRequested -> onBack()
                else -> viewModel.handleEvent(event)
            }
        },
    )
}
