package com.alxnophis.jetpack.filedownloader.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.filedownloader.data.datasource.DownloaderDataSource
import com.alxnophis.jetpack.filedownloader.data.model.FileDownloaderError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class FileDownloaderRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val androidDownloaderDataSource: DownloaderDataSource
) : FileDownloaderRepository {

    private val downloadingFiles = mutableListOf<DownloadingFile>()
    private val downloadedFiles = mutableListOf<DownloadingFile>()

    override suspend fun downloadFile(fileUrl: String): Either<FileDownloaderError, Long> =
        withContext(ioDispatcher) {
            Either.catch(
                { exception ->
                    when (exception) {
                        is FileDownloadedException -> FileDownloaderError.FileDownloaded
                        is FileDownloadingException -> FileDownloaderError.FileDownloading
                        else -> FileDownloaderError.Unknown
                    }
                },
                {
                    when {
                        isFileDownloading(fileUrl) -> throw FileDownloadingException()
                        isFileDownloaded(fileUrl) -> throw FileDownloadedException()
                        else -> {
                            androidDownloaderDataSource
                                .downloadFile(fileUrl)
                                .also { downloadId ->
                                    synchronized(downloadingFiles) {
                                        downloadingFiles.add(DownloadingFile(id = downloadId, url = fileUrl))
                                    }
                                }
                        }
                    }
                }
            )
        }

    override fun fileDownloaded(downloadId: Long) {
        downloadingFiles
            .firstOrNull { downloadingFile -> downloadingFile.id == downloadId }
            ?.let {
                synchronized(downloadingFiles) { downloadingFiles.remove(it) }
                synchronized(downloadedFiles) { downloadedFiles.add(it) }
            }
    }

    private fun isFileDownloading(fileUrl: String): Boolean =
        downloadingFiles
            .firstOrNull { downloadingFile -> downloadingFile.url == fileUrl }
            ?.let { true }
            ?: false

    private fun isFileDownloaded(fileUrl: String): Boolean =
        downloadedFiles
            .firstOrNull { downloadingFile -> downloadingFile.url == fileUrl }
            ?.let { true }
            ?: false
}

private data class DownloadingFile(
    val id: Long,
    val url: String
)

private class FileDownloadingException : Exception()
private class FileDownloadedException : Exception()
