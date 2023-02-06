package com.alxnophis.jetpack.filedownloader.ui.contract

import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState

internal sealed class FileDownloaderEvent : UiEvent {
    object DownloadFile : FileDownloaderEvent()
    data class UrlChanged(val url: String) : FileDownloaderEvent()
}

internal data class FileDownloaderState(
    val url: String = "https://images.pexels.com/photos/1212487/pexels-photo-1212487.jpeg",
) : UiState
