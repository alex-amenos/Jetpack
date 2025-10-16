package com.alxnophis.jetpack.filedownloader.ui.contract

import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState
import com.alxnophis.jetpack.kotlin.constants.ZERO_INT

internal sealed class FileDownloaderUiEvent : UiEvent {
    data object Initialized : FileDownloaderUiEvent()

    data object GoBackRequested : FileDownloaderUiEvent()

    data object DownloadFileRequested : FileDownloaderUiEvent()

    data object ErrorDismissRequested : FileDownloaderUiEvent()

    data class UrlChanged(
        val url: String,
    ) : FileDownloaderUiEvent()
}

@Immutable
@optics
internal data class FileDownloaderUiState(
    val url: String,
    val error: Int,
    val fileStatusList: List<String>,
) : UiState {
    internal companion object Companion {
        val initialState =
            FileDownloaderUiState(
                url = "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_1MB.mp4",
                error = NO_ERROR,
                fileStatusList = emptyList(),
            )
    }
}

internal const val NO_ERROR = ZERO_INT
