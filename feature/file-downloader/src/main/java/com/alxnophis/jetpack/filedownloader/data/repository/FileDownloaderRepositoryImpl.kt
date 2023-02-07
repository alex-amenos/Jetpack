package com.alxnophis.jetpack.filedownloader.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.filedownloader.data.datasource.AndroidDownloader
import com.alxnophis.jetpack.filedownloader.data.model.FileDownloaderError
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import kotlinx.coroutines.withContext

internal class FileDownloaderRepositoryImpl(
    private val androidDownloader: AndroidDownloader,
    private val dispatcherProvider: DispatcherProvider
) : FileDownloaderRepository {

    private val downloadingFiles = mutableListOf<DownloadingFile>()
    private val downloadedFiles = mutableListOf<DownloadingFile>()

    override suspend fun downloadFile(fileUrl: String): Either<FileDownloaderError, Long> =
        withContext(dispatcherProvider.io()) {
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
                            download(fileUrl).also { downloadId ->
                                synchronized(downloadingFiles) {
                                    downloadingFiles.add(DownloadingFile(id = downloadId, url = fileUrl))
                                }
                            }
                        }
                    }
                }
            )
        }

    private fun download(fileUrl: String): Long = androidDownloader.downloadFile(fileUrl)

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
    val url: String,
)

private class FileDownloadingException : Exception()
private class FileDownloadedException : Exception()
