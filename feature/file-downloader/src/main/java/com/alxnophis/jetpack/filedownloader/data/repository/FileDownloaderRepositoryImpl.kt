package com.alxnophis.jetpack.filedownloader.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.filedownloader.data.datasource.DownloaderDataSource
import com.alxnophis.jetpack.filedownloader.data.model.DownloaderFile
import com.alxnophis.jetpack.filedownloader.data.model.FileDownloaderError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

internal class FileDownloaderRepositoryImpl(
    private val androidDownloaderDataSource: DownloaderDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : FileDownloaderRepository {
    override val downloadingFiles: StateFlow<List<DownloaderFile>>
        field = MutableStateFlow(emptyList())

    override val downloadedFiles: StateFlow<List<DownloaderFile>>
        field = MutableStateFlow(emptyList())

    override suspend fun downloadFile(fileUrl: String): Either<FileDownloaderError, Long> =
        withContext(ioDispatcher) {
            Either
                .catch {
                    when {
                        isFileDownloading(fileUrl) -> throw FileDownloadingException()
                        isFileDownloaded(fileUrl) -> throw FileDownloadedException()
                        else -> {
                            androidDownloaderDataSource
                                .downloadFile(fileUrl)
                                .also { downloadId ->
                                    downloadingFiles.update {
                                        it.plus(DownloaderFile(id = downloadId, url = fileUrl))
                                    }
                                }
                        }
                    }
                }.mapLeft { exception ->
                    when (exception) {
                        is FileDownloadedException -> FileDownloaderError.FileDownloaded
                        is FileDownloadingException -> FileDownloaderError.FileDownloading
                        else -> FileDownloaderError.Unknown
                    }
                }
        }

    override fun fileDownloaded(downloadId: Long) {
        val downloadedFile = downloadingFiles.value.filter { it.id == downloadId }
        downloadingFiles.update {
            it.filterNot { item -> item.id == downloadId }
        }
        downloadedFiles.update {
            it.plus(downloadedFile)
        }
    }

    private fun isFileDownloading(fileUrl: String): Boolean =
        downloadingFiles
            .value
            .firstOrNull { downloadingFile -> downloadingFile.url == fileUrl }
            ?.let { true }
            ?: false

    private fun isFileDownloaded(fileUrl: String): Boolean =
        downloadedFiles
            .value
            .firstOrNull { downloadingFile -> downloadingFile.url == fileUrl }
            ?.let { true }
            ?: false
}

private class FileDownloadingException : Exception()

private class FileDownloadedException : Exception()
