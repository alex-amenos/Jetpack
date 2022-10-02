package com.alxnophis.jetpack.core.extensions

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.alxnophis.jetpack.core.base.constants.WHITE_SPACE
import com.alxnophis.jetpack.core.base.constants.EMPTY
import com.alxnophis.jetpack.core.base.constants.PARENTHESES_CLOSED
import com.alxnophis.jetpack.core.base.constants.PARENTHESES_OPENED
import com.alxnophis.jetpack.kotlin.constants.ZERO_INT
import com.alxnophis.jetpack.kotlin.constants.ZERO_LONG

fun Context.getVersionName(): String =
    try {
        val packageInfo: PackageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(ZERO_LONG))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, ZERO_INT)
        }
        packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        EMPTY
    }

@Suppress("DEPRECATION")
fun Context.getVersionCode(): String =
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageManager.getPackageInfo(packageName, 0).longVersionCode.toString()
        } else {
            packageManager.getPackageInfo(packageName, 0).versionCode.toString()
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        EMPTY
    }

fun Context.getVersion(): String = StringBuilder()
    .append(this.getVersionName())
    .append(WHITE_SPACE)
    .append(PARENTHESES_OPENED)
    .append(this.getVersionCode())
    .append(PARENTHESES_CLOSED)
    .toString()
