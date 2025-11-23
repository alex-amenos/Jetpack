package com.alxnophis.jetpack.posts.di

import com.alxnophis.jetpack.posts.data.repository.PostsRepository
import com.alxnophis.jetpack.posts.data.repository.PostsRepositoryImpl
import com.alxnophis.jetpack.posts.ui.viewmodel.PostsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val postsModule: Module =
    module {
        factory<PostsRepository> { PostsRepositoryImpl(get()) }
        viewModel { PostsViewModel(get()) }
    }
