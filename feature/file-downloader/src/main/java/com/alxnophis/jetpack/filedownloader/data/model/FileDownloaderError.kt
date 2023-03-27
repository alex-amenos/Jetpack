package com.alxnophis.jetpack.filedownloader.data.model

sealed class FileDownloaderError {
    object FileDownloading : FileDownloaderError()
    object FileDownloaded : FileDownloaderError()
    object Unknown : FileDownloaderError()
}
