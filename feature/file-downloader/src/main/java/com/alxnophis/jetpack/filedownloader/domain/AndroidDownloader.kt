package com.alxnophis.jetpack.filedownloader.domain

interface AndroidDownloader {
    fun downloadFile(url: String): Long
}
