package com.alxnophis.jetpack.filedownloader.domain

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import com.alxnophis.jetpack.core.base.formatter.BaseDateFormatter
import com.alxnophis.jetpack.filedownloader.R
import com.alxnophis.jetpack.kotlin.constants.DOT
import java.util.Date

internal class AndroidDownloaderImpl(
    private val context: Context,
    private val dateFormatter: BaseDateFormatter
) : AndroidDownloader {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    override fun downloadFile(url: String): Long {
        val fileExtensions = "jpeg"
        val filename = buildString {
            append(dateFormatter.formatToReadableDateTimeForFile(Date()))
            append(DOT)
            append(fileExtensions)
        }
        val title = context.getString(R.string.app_name)
        val request = DownloadManager
            .Request(url.toUri())
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setMimeType("image/jpeg")
            .setTitle(title)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
        return downloadManager.enqueue(request)
    }
}
