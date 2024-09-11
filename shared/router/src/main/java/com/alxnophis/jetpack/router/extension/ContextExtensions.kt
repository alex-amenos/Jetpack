package com.alxnophis.jetpack.router.extension

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

private const val ROOT_ACTIVITY = "com.alxnophis.jetpack.root.ui.RootActivity"

fun Context.appPendingIntent(): PendingIntent {
    val intent = Intent().setClassName(this, ROOT_ACTIVITY).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
    return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
}
