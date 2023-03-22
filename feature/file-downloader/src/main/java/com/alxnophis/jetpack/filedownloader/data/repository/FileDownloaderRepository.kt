package com.alxnophis.jetpack.filedownloader.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.filedownloader.data.model.DownloaderFile
import com.alxnophis.jetpack.filedownloader.data.model.FileDownloaderError
import kotlinx.coroutines.flow.StateFlow

internal interface FileDownloaderRepository {
    val downloadingFiles: StateFlow<List<DownloaderFile>>
    val downloadedFiles: StateFlow<List<DownloaderFile>>
    suspend fun downloadFile(fileUrl: String): Either<FileDownloaderError, Long>
    fun fileDownloaded(downloadId: Long)
}
