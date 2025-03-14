package com.alxnophis.jetpack.filedownloader.ui.contract

import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.kotlin.constants.ZERO_INT

internal sealed class FileDownloaderEvent : UiEvent {
    data object Initialized : FileDownloaderEvent()

    data object GoBackRequested : FileDownloaderEvent()

    data object DownloadFileRequested : FileDownloaderEvent()

    data object ErrorDismissRequested : FileDownloaderEvent()

    data class UrlChanged(
        val url: String,
    ) : FileDownloaderEvent()
}

@optics
internal data class FileDownloaderState(
    val url: String,
    val error: Int,
    val fileStatusList: List<String>,
) : UiState {
    internal companion object {
        val initialState =
            FileDownloaderState(
                url = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_20mb.mp4",
                error = NO_ERROR,
                fileStatusList = emptyList(),
            )
    }
}

internal const val NO_ERROR = ZERO_INT
