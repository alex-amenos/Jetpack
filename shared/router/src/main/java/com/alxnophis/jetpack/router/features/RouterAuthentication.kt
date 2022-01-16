package com.alxnophis.jetpack.router.features

import android.content.Intent
import com.alxnophis.jetpack.router.base.loadIntentOrNull

object RouterAuthentication : DynamicFeature<Intent> {

    private const val MODULE_ACTIVITY_PATH = "com.alxnophis.jetpack.authentication.ui.view"
    private const val ACTIVITY_AUTHENTICATION = "$MODULE_ACTIVITY_PATH.AuthenticationActivity"
    private const val ACTIVITY_AUTHORIZED = "$MODULE_ACTIVITY_PATH.AuthorizedActivity"

    override val dynamicStart: Intent?
        get() = ACTIVITY_AUTHENTICATION.loadIntentOrNull()

    val activityAuthorized: Intent?
        get() = ACTIVITY_AUTHORIZED.loadIntentOrNull()
}
