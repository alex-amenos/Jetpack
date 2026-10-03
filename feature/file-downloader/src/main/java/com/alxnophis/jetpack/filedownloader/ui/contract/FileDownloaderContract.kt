package com.alxnophis.jetpack.filedownloader.ui.contract

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import arrow.optics.optics
import com.alxnophis.jetpack.core.ui.viewmodel.UiEvent
import com.alxnophis.jetpack.core.ui.viewmodel.UiState

internal sealed class FileDownloaderUiEvent : UiEvent {
    data object DownloadFileRequested : FileDownloaderUiEvent()

    data class ErrorDismissRequested(
        val errorId: Long = 0L,
    ) : FileDownloaderUiEvent()

    data class UrlChanged(
        val url: String,
    ) : FileDownloaderUiEvent()
}

@Immutable
internal data class FileDownloaderUiError(
    val id: Long = System.currentTimeMillis(),
    @param:StringRes val messageRes: Int,
)

@optics
@Immutable
internal data class FileDownloaderUiState(
    val url: String,
    val error: FileDownloaderUiError?,
    val fileStatusList: List<String>,
) : UiState {
    internal companion object {
        val initialState =
            FileDownloaderUiState(
                url = "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_1MB.mp4",
                error = null,
                fileStatusList = emptyList(),
            )
    }
}
