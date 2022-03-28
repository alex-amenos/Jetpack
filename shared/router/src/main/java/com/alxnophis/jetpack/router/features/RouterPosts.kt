package com.alxnophis.jetpack.router.features

import android.content.Intent
import com.alxnophis.jetpack.router.base.loadIntentOrNull

object RouterPosts : DynamicFeature<Intent> {

    private const val PACKAGE_NAME = "com.alxnophis.jetpack.posts.ui"
    private const val ACTIVITY_POSTS = "$PACKAGE_NAME.PostsActivity"

    override val dynamicStart: Intent?
        get() = ACTIVITY_POSTS.loadIntentOrNull()
}
