package com.alxnophis.jetpack.router.base

import android.content.Intent
import com.alxnophis.jetpack.router.BuildConfig

private fun intentTo(className: String): Intent {
    return Intent(Intent.ACTION_VIEW).setClassName(BuildConfig.APP_PACKAGE_NAME, className)
}

internal fun String.loadIntentOrNull(): Intent? {
    return try {
        Class.forName(this).run { intentTo(this@loadIntentOrNull) }
    } catch (e: ClassNotFoundException) {
        null
    }
}
