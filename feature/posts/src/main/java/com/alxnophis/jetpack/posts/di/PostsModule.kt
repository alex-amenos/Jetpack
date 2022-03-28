package com.alxnophis.jetpack.posts.di

import com.alxnophis.jetpack.core.di.coreModule
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

fun injectPosts() = loadPostsModules

private val loadPostsModules by lazy {
    loadKoinModules(
        listOf(
            coreModule,
            postModule
        )
    )
}

private val postModule: Module = module {

}
