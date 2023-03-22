package com.alxnophis.jetpack.filedownloader.ui.contract

import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState

internal sealed class FileDownloaderEvent : UiEvent {
    object DownloadFile : FileDownloaderEvent()
    object ErrorDismissed : FileDownloaderEvent()
    data class UrlChanged(val url: String) : FileDownloaderEvent()
}

internal data class FileDownloaderState(
    val url: String = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_20mb.mp4",
    val error: Int? = null,
    val fileStatusList: List<String> = emptyList()
) : UiState
