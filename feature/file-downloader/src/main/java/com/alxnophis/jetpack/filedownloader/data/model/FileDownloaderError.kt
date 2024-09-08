package com.alxnophis.jetpack.filedownloader.data.model

sealed class FileDownloaderError {
    data object FileDownloading : FileDownloaderError()
    data object FileDownloaded : FileDownloaderError()
    data object Unknown : FileDownloaderError()
}
