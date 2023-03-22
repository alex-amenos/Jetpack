package com.alxnophis.jetpack.filedownloader.data.datasource

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.alxnophis.jetpack.core.base.formatter.BaseDateFormatter
import com.alxnophis.jetpack.filedownloader.R
import com.alxnophis.jetpack.kotlin.constants.DOT
import java.net.URL
import java.net.URLConnection
import java.util.Date

typealias FileUrl = String
typealias ContentType = String

internal class AndroidDownloaderDataSourceImpl(
    private val context: Context,
    private val dateFormatter: BaseDateFormatter
) : DownloaderDataSource {

    private val downloadManager: DownloadManager = context.getSystemService(DownloadManager::class.java)

    override fun downloadFile(fileUlr: String): Long {
        val mimeType = fileUlr.getContentType()
        val fileExtensions = mimeType.getExtension()
        val filename = buildString {
            append(dateFormatter.formatToReadableDateTimeSnakeCase(Date()))
            fileExtensions?.let {
                append(DOT)
                append(fileExtensions)
            }
        }
        val title = context.getString(R.string.app_name)
        val request = DownloadManager
            .Request(fileUlr.toUri())
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setMimeType(mimeType)
            .setTitle(title)
            .setDescription(fileUlr)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
        return downloadManager.enqueue(request)
    }

    private fun FileUrl.getContentType(): String {
        val urlConnection: URLConnection = URL(this).openConnection()
        return urlConnection.contentType
    }

    private fun ContentType.getExtension(): String? =
        MimeTypeMap.getSingleton().getExtensionFromMimeType(this)
}
