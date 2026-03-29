package com.alxnophis.jetpack.posts.di

import com.alxnophis.jetpack.posts.data.datasource.PostsDataSource
import com.alxnophis.jetpack.posts.data.datasource.PostsRemoteDataSource
import com.alxnophis.jetpack.posts.data.repository.PostsRepository
import com.alxnophis.jetpack.posts.data.repository.PostsRepositoryImpl
import com.alxnophis.jetpack.posts.ui.viewmodel.PostDetailViewModel
import com.alxnophis.jetpack.posts.ui.viewmodel.PostsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val postsModule: Module = module {
    factory<PostsDataSource> { PostsRemoteDataSource(get()) }
    factory<PostsRepository> { PostsRepositoryImpl(get()) }
    viewModel { PostsViewModel(get()) }
    viewModel { PostDetailViewModel(get()) }
}
