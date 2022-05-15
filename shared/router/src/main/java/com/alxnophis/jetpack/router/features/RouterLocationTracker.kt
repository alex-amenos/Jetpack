package com.alxnophis.jetpack.router.features

import android.content.Intent
import com.alxnophis.jetpack.router.base.loadIntentOrNull

object RouterLocationTracker : DynamicFeature<Intent> {

    private const val PACKAGE_NAME = "com.alxnophis.jetpack.location.tracker.ui"
    private const val ACTIVITY_LOCATION_TRACKER = "$PACKAGE_NAME.LocationTrackerActivity"

    override val dynamicStart: Intent?
        get() = ACTIVITY_LOCATION_TRACKER.loadIntentOrNull()
}
