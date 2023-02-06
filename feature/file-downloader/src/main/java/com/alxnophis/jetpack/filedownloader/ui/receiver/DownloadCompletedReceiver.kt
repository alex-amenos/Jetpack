package com.alxnophis.jetpack.filedownloader.ui.receiver

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class DownloadCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, DEFAULT_ID)
            if (id != DEFAULT_ID) {
                Timber.d("Download with ID $id finished!")
            }
        }
    }

    companion object {
        private const val ACTION = "android.intent.action.DOWNLOAD_COMPLETE"
        private const val DEFAULT_ID = -1L
    }
}
