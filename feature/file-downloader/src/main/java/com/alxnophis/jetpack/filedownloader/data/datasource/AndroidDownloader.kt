package com.alxnophis.jetpack.filedownloader.data.datasource

interface AndroidDownloader {
    fun downloadFile(url: String): Long
}
