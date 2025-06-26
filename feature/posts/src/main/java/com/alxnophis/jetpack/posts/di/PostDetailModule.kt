package com.alxnophis.jetpack.posts.di

import com.alxnophis.jetpack.posts.data.repository.PostsRepository
import com.alxnophis.jetpack.posts.data.repository.PostsRepositoryImpl
import com.alxnophis.jetpack.posts.ui.viewmodel.PostDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

fun injectPostDetail() = loadPostDetailModules

private val loadPostDetailModules by lazy {
    loadKoinModules(postDetailModule)
}

private val postDetailModule: Module =
    module {
        factory<PostsRepository> { PostsRepositoryImpl(get()) }
        viewModel { PostDetailViewModel(get()) }
    }
