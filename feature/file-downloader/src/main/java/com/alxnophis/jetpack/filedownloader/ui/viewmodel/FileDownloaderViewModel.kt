package com.alxnophis.jetpack.filedownloader.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.core.extensions.doNothing
import com.alxnophis.jetpack.core.extensions.isValidUrl
import com.alxnophis.jetpack.filedownloader.domain.AndroidDownloader
import com.alxnophis.jetpack.filedownloader.ui.contract.FileDownloaderEvent
import com.alxnophis.jetpack.filedownloader.ui.contract.FileDownloaderState
import kotlinx.coroutines.launch
import timber.log.Timber

internal class FileDownloaderViewModel(
    initialState: FileDownloaderState,
    private val downloader: AndroidDownloader,
) : BaseViewModel<FileDownloaderEvent, FileDownloaderState>(initialState) {

    override fun handleEvent(event: FileDownloaderEvent) {
        Timber.d("## FileDownloaderViewModel handle event: $event")
        viewModelScope.launch {
            when (event) {
                is FileDownloaderEvent.UrlChanged -> updateUrl(event.url)
                is FileDownloaderEvent.DownloadFile -> downloadFile()
            }
        }
    }

    private fun updateUrl(url: String) {
        viewModelScope.launch {
            updateState {
                currentState.copy(url = url)
            }
        }
    }

    private fun downloadFile() {
        viewModelScope.launch {
            val urlFile = currentState.url
            when {
                urlFile.isValidUrl() -> downloader.downloadFile(urlFile)
                else -> doNothing()
            }
        }
    }
}
