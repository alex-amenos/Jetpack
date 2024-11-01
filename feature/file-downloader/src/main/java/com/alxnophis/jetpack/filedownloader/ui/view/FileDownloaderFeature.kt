package com.alxnophis.jetpack.filedownloader.ui.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.filedownloader.di.injectFileDownloader
import com.alxnophis.jetpack.filedownloader.ui.contract.FileDownloaderEvent
import com.alxnophis.jetpack.filedownloader.ui.viewmodel.FileDownloaderViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun FileDownloaderFeature(
    onBack: () -> Unit,
) {
    injectFileDownloader()
    val viewModel = getViewModel<FileDownloaderViewModel>()
    FileDownloaderScreen(
        state = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            when (event) {
                FileDownloaderEvent.GoBackRequested -> onBack()
                else -> viewModel.handleEvent(event)
            }
        },
    )
}
