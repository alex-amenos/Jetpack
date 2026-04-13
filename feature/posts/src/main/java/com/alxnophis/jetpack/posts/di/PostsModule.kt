package com.alxnophis.jetpack.posts.di

import androidx.room.Room
import com.alxnophis.jetpack.posts.data.database.PostDao
import com.alxnophis.jetpack.posts.data.database.PostsDatabase
import com.alxnophis.jetpack.posts.data.database.PostsMetadataDao
import com.alxnophis.jetpack.posts.data.datasource.PostsLocalDataSource
import com.alxnophis.jetpack.posts.data.datasource.PostsLocalDataSourceImpl
import com.alxnophis.jetpack.posts.data.datasource.PostsRemoteDataSource
import com.alxnophis.jetpack.posts.data.datasource.PostsRemoteDataSourceImp
import com.alxnophis.jetpack.posts.data.repository.PostsRepository
import com.alxnophis.jetpack.posts.data.repository.PostsRepositoryImpl
import com.alxnophis.jetpack.posts.ui.viewmodel.PostDetailViewModel
import com.alxnophis.jetpack.posts.ui.viewmodel.PostsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val postsModule: Module =
    module {
        // Database
        single<PostsDatabase> {
            Room
                .databaseBuilder(
                    androidContext(),
                    PostsDatabase::class.java,
                    PostsDatabase.DATABASE_NAME,
                ).build()
        }

        // DAOs
        single<PostDao> { get<PostsDatabase>().postDao() }
        single<PostsMetadataDao> { get<PostsDatabase>().postsMetadataDao() }

        // Data Sources
        factory<PostsRemoteDataSource> { PostsRemoteDataSourceImp(get()) }
        factory<PostsLocalDataSource> { PostsLocalDataSourceImpl(get(), get()) }

        // Repository
        // Uses application-level scope for background refresh to prevent coroutine leaks
        single<PostsRepository> {
            PostsRepositoryImpl(
                remoteDataSource = get(),
                localDataSource = get(),
                backgroundRefreshScope = get(named("applicationScope")),
            )
        }

        // ViewModels
        viewModel { PostsViewModel(get()) }
        viewModel { PostDetailViewModel(get()) }
    }
