package com.alxnophis.jetpack.router.features

import android.content.Intent
import com.alxnophis.jetpack.router.base.loadIntentOrNull

object RouterAuthentication : DynamicFeature<Intent> {

    private const val PACKAGE_NAME = "com.alxnophis.jetpack.authentication.ui"
    private const val ACTIVITY_AUTHENTICATION = "$PACKAGE_NAME.AuthenticationActivity"
    private const val ACTIVITY_AUTHORIZED = "$PACKAGE_NAME.AuthorizedActivity"

    override val dynamicStart: Intent?
        get() = ACTIVITY_AUTHENTICATION.loadIntentOrNull()

    val activityAuthorized: Intent?
        get() = ACTIVITY_AUTHORIZED.loadIntentOrNull()
}
