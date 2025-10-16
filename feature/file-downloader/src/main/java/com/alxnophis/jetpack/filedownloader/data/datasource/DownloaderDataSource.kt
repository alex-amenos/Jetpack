package com.alxnophis.jetpack.filedownloader.data.datasource

fun interface DownloaderDataSource {
    fun downloadFile(url: String): Long
}
