package com.alxnophis.jetpack.filedownloader.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.filedownloader.data.model.FileDownloaderError

interface FileDownloaderRepository {
    suspend fun downloadFile(fileUrl: String): Either<FileDownloaderError, Long>
    fun fileDownloaded(downloadId: Long)
}
