package com.alxnophis.jetpack.router.features

import android.content.Intent
import com.alxnophis.jetpack.router.base.loadIntentOrNull

object RouterAuthentication : DynamicFeature<Intent> {

    private const val MODULE_ACTIVITY_PATH = "com.alxnophis.jetpack.authentication.ui.view"
    private const val HOME_COMPOSE_ACTIVITY = "$MODULE_ACTIVITY_PATH.AuthenticationActivity"

    override val dynamicStart: Intent?
        get() {
            return HOME_COMPOSE_ACTIVITY.loadIntentOrNull()
        }
}
