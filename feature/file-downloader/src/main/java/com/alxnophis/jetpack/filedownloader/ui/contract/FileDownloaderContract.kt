package com.alxnophis.jetpack.filedownloader.ui.contract

import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState
import com.alxnophis.jetpack.kotlin.constants.EMPTY

internal sealed class FileDownloaderEvent : UiEvent {
    object DownloadFile : FileDownloaderEvent()
    data class UrlChanged(val url: String) : FileDownloaderEvent()
}

internal data class FileDownloaderState(
    val url: String = EMPTY,
) : UiState
