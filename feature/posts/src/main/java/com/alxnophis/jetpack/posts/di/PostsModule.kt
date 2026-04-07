package com.alxnophis.jetpack.posts.di

import androidx.room.Room
import com.alxnophis.jetpack.posts.data.database.PostDao
import com.alxnophis.jetpack.posts.data.database.PostsDatabase
import com.alxnophis.jetpack.posts.data.datasource.PostsLocalDataSource
import com.alxnophis.jetpack.posts.data.datasource.PostsLocalDataSourceImpl
import com.alxnophis.jetpack.posts.data.datasource.PostsRemoteDataSource
import com.alxnophis.jetpack.posts.data.datasource.PostsRemoteRemoteDataSourceImp
import com.alxnophis.jetpack.posts.data.repository.PostsRepository
import com.alxnophis.jetpack.posts.data.repository.PostsRepositoryImpl
import com.alxnophis.jetpack.posts.ui.viewmodel.PostDetailViewModel
import com.alxnophis.jetpack.posts.ui.viewmodel.PostsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val postsModule: Module =
    module {
        // Database
        single<PostsDatabase> {
            Room.databaseBuilder(
                androidContext(),
                PostsDatabase::class.java,
                PostsDatabase.DATABASE_NAME,
            ).build()
        }

        // DAO
        single<PostDao> { get<PostsDatabase>().postDao() }

        // Data Sources
        factory<PostsRemoteDataSource> { PostsRemoteRemoteDataSourceImp(get()) }
        factory<PostsLocalDataSource> { PostsLocalDataSourceImpl(get()) }

        // Repository
        factory<PostsRepository> { PostsRepositoryImpl(get(), get()) }

        // ViewModels
        viewModel { PostsViewModel(get()) }
        viewModel { PostDetailViewModel(get()) }
    }
