package com.alxnophis.jetpack.filedownloader.ui.contract

import arrow.optics.optics
import com.alxnophis.jetpack.core.base.viewmodel.UiEvent
import com.alxnophis.jetpack.core.base.viewmodel.UiState
import com.alxnophis.jetpack.kotlin.constants.ZERO_INT

internal sealed class FileDownloaderEvent : UiEvent {
    object DownloadFile : FileDownloaderEvent()
    object ErrorDismissed : FileDownloaderEvent()
    data class UrlChanged(val url: String) : FileDownloaderEvent()
}

@optics
internal data class FileDownloaderState(
    val url: String = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_20mb.mp4",
    val error: Int = NO_ERROR,
    val fileStatusList: List<String> = emptyList()
) : UiState {
    internal companion object
}

internal const val NO_ERROR = ZERO_INT
