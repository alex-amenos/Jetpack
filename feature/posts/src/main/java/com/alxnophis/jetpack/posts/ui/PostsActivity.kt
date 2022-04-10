package com.alxnophis.jetpack.posts.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import com.alxnophis.jetpack.core.base.activity.BaseActivity
import com.alxnophis.jetpack.core.extensions.repeatOnLifecycleResumed
import com.alxnophis.jetpack.posts.di.injectPosts
import com.alxnophis.jetpack.posts.ui.contract.PostsEffect
import com.alxnophis.jetpack.posts.ui.view.PostScreen
import com.alxnophis.jetpack.posts.ui.viewmodel.PostsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PostsActivity : BaseActivity() {

    private val viewModel: PostsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectPosts()
        initSideEffectObserver()
        renderContent()
    }

    private fun initSideEffectObserver() = repeatOnLifecycleResumed {
        viewModel.effect.collect { effect ->
            when (effect) {
                PostsEffect.Finish -> this.finish()
            }
        }
    }

    private fun renderContent() {
        setContent {
            PostScreen()
        }
    }
}
