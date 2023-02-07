package com.alxnophis.jetpack.filedownloader.data.repository

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.java.KoinJavaComponent.inject
import timber.log.Timber

class DownloadCompletedReceiver : BroadcastReceiver() {

    private val fileDownloaderRepository: FileDownloaderRepository by inject(FileDownloaderRepository::class.java)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, DEFAULT_ID)
            if (id != DEFAULT_ID) {
                fileDownloaderRepository.fileDownloaded(id)
                Timber.d("Download with ID $id finished!")
            }
        }
    }

    companion object {
        private const val ACTION = "android.intent.action.DOWNLOAD_COMPLETE"
        private const val DEFAULT_ID = -1L
    }
}
