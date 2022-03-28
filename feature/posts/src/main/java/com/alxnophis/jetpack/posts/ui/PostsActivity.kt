package com.alxnophis.jetpack.posts.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import com.alxnophis.jetpack.core.base.activity.BaseActivity
import com.alxnophis.jetpack.core.extensions.repeatOnLifecycleResumed
import com.alxnophis.jetpack.posts.di.injectPosts
import com.alxnophis.jetpack.posts.ui.view.PostsComposable

class PostsActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectPosts()
        initSideEffectObserver()
        renderContent()
    }

    private fun initSideEffectObserver() = repeatOnLifecycleResumed {
        // viewModel side effect
    }

    private fun renderContent() {
        setContent {
            PostsComposable()
        }
    }
}
