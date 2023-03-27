package com.alxnophis.jetpack.filedownloader.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.core.extensions.isValidUrl
import com.alxnophis.jetpack.filedownloader.R
import com.alxnophis.jetpack.filedownloader.data.model.DownloaderFile
import com.alxnophis.jetpack.filedownloader.data.model.FileDownloaderError
import com.alxnophis.jetpack.filedownloader.data.repository.FileDownloaderRepository
import com.alxnophis.jetpack.filedownloader.ui.contract.FileDownloaderEvent
import com.alxnophis.jetpack.filedownloader.ui.contract.FileDownloaderState
import com.alxnophis.jetpack.kotlin.constants.EMPTY
import com.alxnophis.jetpack.kotlin.constants.WHITE_SPACE
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber

internal class FileDownloaderViewModel(
    initialState: FileDownloaderState = FileDownloaderState(),
    private val fileDownloaderRepository: FileDownloaderRepository
) : BaseViewModel<FileDownloaderEvent, FileDownloaderState>(initialState) {

    init {
        subscribeToDownloaderFilesStatus()
    }

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

    private fun subscribeToDownloaderFilesStatus() = viewModelScope.launch {
        fileDownloaderRepository
            .downloadingFiles
            .combine(fileDownloaderRepository.downloadedFiles) { downloadingFiles, downloadedFiles ->
                val downloadingList = downloadingFiles.map { it.mapTo(DOWNLOADING_STATUS) }
                val downloadedList = downloadedFiles.map { it.mapTo(DOWNLOADED_STATUS) }
                updateState {
                    copy(fileStatusList = downloadingList + downloadedList)
                }
            }
            .collect()
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

    private fun DownloaderFile.mapTo(status: String) = buildString {
        append(status)
        append(WHITE_SPACE)
        append(url)
    }

    companion object {
        private const val DOWNLOADING_STATUS = "⏳"
        private const val DOWNLOADED_STATUS = "✅"
    }
}
