package com.alxnophis.jetpack.filedownloader.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.core.extensions.isValidUrl
import com.alxnophis.jetpack.filedownloader.R
import com.alxnophis.jetpack.filedownloader.data.model.FileDownloaderError
import com.alxnophis.jetpack.filedownloader.data.repository.FileDownloaderRepository
import com.alxnophis.jetpack.filedownloader.ui.contract.FileDownloaderEvent
import com.alxnophis.jetpack.filedownloader.ui.contract.FileDownloaderState
import com.alxnophis.jetpack.kotlin.constants.EMPTY
import kotlinx.coroutines.launch
import timber.log.Timber

internal class FileDownloaderViewModel(
    initialState: FileDownloaderState,
    private val fileDownloaderRepository: FileDownloaderRepository,
) : BaseViewModel<FileDownloaderEvent, FileDownloaderState>(initialState) {

    override fun handleEvent(event: FileDownloaderEvent) {
        Timber.d("## FileDownloaderViewModel handle event: $event")
        viewModelScope.launch {
            when (event) {
                is FileDownloaderEvent.UrlChanged -> updateUrl(event.url)
                is FileDownloaderEvent.DownloadFile -> downloadFile()
                is FileDownloaderEvent.ErrorDismissed -> updateState { copy(error = null) }
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
            if (urlFile.isValidUrl()) {
                fileDownloaderRepository
                    .downloadFile(urlFile)
                    .fold(
                        { error ->
                            val errorResId = when (error) {
                                FileDownloaderError.FileDownloaded -> R.string.file_downloader_file_downloaded
                                FileDownloaderError.FileDownloading -> R.string.file_downloader_file_downloading
                                FileDownloaderError.Unknown -> R.string.file_downloader_generic_error
                            }
                            updateState {
                                currentState.copy(error = errorResId)
                            }
                        },
                        {
                            updateState {
                                currentState.copy(url = EMPTY)
                            }
                        }
                    )
            }
        }
    }
}
