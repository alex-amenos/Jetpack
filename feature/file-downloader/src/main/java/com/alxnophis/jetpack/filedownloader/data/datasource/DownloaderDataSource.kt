package com.alxnophis.jetpack.filedownloader.data.datasource

interface DownloaderDataSource {
    fun downloadFile(url: String): Long
}
