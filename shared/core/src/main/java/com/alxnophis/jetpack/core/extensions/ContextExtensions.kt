package com.alxnophis.jetpack.core.extensions

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import com.alxnophis.jetpack.core.base.constants.EMPTY
import com.alxnophis.jetpack.kotlin.constants.ZERO_INT

fun Context.getVersionName(): String =
    try {
        val packageInfo: PackageInfo =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(packageName, ZERO_INT)
            }
        packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        EMPTY
    }

fun Context.getVersion(): String =
    StringBuilder()
        .append(this.getVersionName())
        .toString()

fun Context.showNotification(
    @StringRes titleResId: Int,
    @StringRes contentResId: Int,
    @DrawableRes icon: Int,
    channelId: String,
    notificationId: Int,
    vibratePattern: LongArray = longArrayOf(1000),
    visibility: Int = NotificationCompat.VISIBILITY_PRIVATE,
    priority: Int = NotificationCompat.PRIORITY_DEFAULT,
    category: String = NotificationCompat.CATEGORY_MESSAGE,
    contentIntent: PendingIntent? = null,
) {
    val notificationManage = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notification =
        NotificationCompat
            .Builder(applicationContext, channelId)
            .setContentTitle(getString(titleResId))
            .setContentText(getString(contentResId))
            .setSmallIcon(icon)
            .setVisibility(visibility)
            .setPriority(priority)
            .setCategory(category)
            .setContentIntent(contentIntent)
            .setVibrate(vibratePattern)
            .build()
    notificationManage.notify(notificationId, notification)
}
