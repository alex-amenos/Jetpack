package com.alxnophis.jetpack.posts.di

import com.alxnophis.jetpack.posts.data.repository.PostsRepository
import com.alxnophis.jetpack.posts.data.repository.PostsRepositoryImpl
import com.alxnophis.jetpack.posts.domain.usecase.PostsUseCase
import com.alxnophis.jetpack.posts.ui.contract.PostsState
import com.alxnophis.jetpack.posts.ui.viewmodel.PostsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

fun injectPosts() = loadPostsModules

private val loadPostsModules by lazy {
    loadKoinModules(postModule)
}

private val postModule: Module = module {
    factory<PostsRepository> { PostsRepositoryImpl(apiDataSource = get()) }
    factory { PostsUseCase(repository = get()) }
    viewModel {
        PostsViewModel(
            initialState = PostsState.initialState,
            postsUseCase = get()
        )
    }
}
