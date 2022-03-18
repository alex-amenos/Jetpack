package com.alxnophis.jetpack.router.features

import android.content.Intent
import com.alxnophis.jetpack.router.base.loadIntentOrNull

object RouterSettings : DynamicFeature<Intent> {

    private const val PACKAGE_NAME = "com.alxnophis.jetpack.settings.ui"
    private const val ACTIVITY_SETTINGS = "$PACKAGE_NAME.SettingsActivity"

    override val dynamicStart: Intent?
        get() = ACTIVITY_SETTINGS.loadIntentOrNull()
}
