package com.alxnophis.jetpack.filedownloader.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.optics.copy
import com.alxnophis.jetpack.core.extensions.isValidUrl
import com.alxnophis.jetpack.core.ui.viewmodel.BaseViewModel
import com.alxnophis.jetpack.filedownloader.R
import com.alxnophis.jetpack.filedownloader.data.model.DownloaderFile
import com.alxnophis.jetpack.filedownloader.data.model.FileDownloaderError
import com.alxnophis.jetpack.filedownloader.data.repository.FileDownloaderRepository
import com.alxnophis.jetpack.filedownloader.ui.contract.FileDownloaderEvent
import com.alxnophis.jetpack.filedownloader.ui.contract.FileDownloaderState
import com.alxnophis.jetpack.filedownloader.ui.contract.NO_ERROR
import com.alxnophis.jetpack.filedownloader.ui.contract.error
import com.alxnophis.jetpack.filedownloader.ui.contract.fileStatusList
import com.alxnophis.jetpack.filedownloader.ui.contract.url
import com.alxnophis.jetpack.kotlin.constants.EMPTY
import com.alxnophis.jetpack.kotlin.constants.WHITE_SPACE
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber

internal class FileDownloaderViewModel(
    private val fileDownloaderRepository: FileDownloaderRepository,
    initialState: FileDownloaderState = FileDownloaderState.initialState,
) : BaseViewModel<FileDownloaderEvent, FileDownloaderState>(initialState) {
    override fun handleEvent(event: FileDownloaderEvent) {
        Timber.d("## FileDownloaderViewModel handle event: $event")
        viewModelScope.launch {
            when (event) {
                FileDownloaderEvent.Initialized -> subscribeToDownloaderFilesStatus()
                FileDownloaderEvent.GoBackRequested -> throw IllegalStateException("Go back not implemented")
                is FileDownloaderEvent.UrlChanged -> updateUrl(event.url)
                is FileDownloaderEvent.DownloadFileRequested -> downloadFile()
                is FileDownloaderEvent.ErrorDismissRequested -> dismissError()
            }
        }
    }

    private fun subscribeToDownloaderFilesStatus() =
        viewModelScope.launch {
            fileDownloaderRepository
                .downloadingFiles
                .combine(fileDownloaderRepository.downloadedFiles) { downloadingFiles, downloadedFiles ->
                    val downloadingList = downloadingFiles.map { it.mapTo(DOWNLOADING_STATUS) }
                    val downloadedList = downloadedFiles.map { it.mapTo(DOWNLOADED_STATUS) }
                    updateUiState {
                        copy {
                            FileDownloaderState.fileStatusList set (downloadingList + downloadedList)
                        }
                    }
                }.collect()
        }

    private fun updateUrl(url: String) {
        viewModelScope.launch {
            updateUiState {
                copy {
                    FileDownloaderState.url set url
                }
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
                            val errorResId =
                                when (error) {
                                    FileDownloaderError.FileDownloaded -> R.string.file_downloader_file_downloaded
                                    FileDownloaderError.FileDownloading -> R.string.file_downloader_file_downloading
                                    FileDownloaderError.Unknown -> R.string.file_downloader_generic_error
                                }
                            updateUiState {
                                copy {
                                    FileDownloaderState.error set errorResId
                                }
                            }
                        },
                        {
                            updateUiState {
                                copy {
                                    FileDownloaderState.url set EMPTY
                                }
                            }
                        },
                    )
            }
        }
    }

    private fun dismissError() {
        updateUiState {
            copy {
                FileDownloaderState.error set NO_ERROR
            }
        }
    }

    private fun DownloaderFile.mapTo(status: String) =
        buildString {
            append(status)
            append(WHITE_SPACE)
            append(url)
        }

    companion object {
        private const val DOWNLOADING_STATUS = "⏳"
        private const val DOWNLOADED_STATUS = "✅"
    }
}
