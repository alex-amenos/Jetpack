package com.alxnophis.jetpack.api.jsonplaceholder.di

import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderAlbumsService
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderCommentsService
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderPhotosService
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderPostsService
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderRetrofitFactory
import com.alxnophis.jetpack.api.jsonplaceholder.JsonPlaceholderUsersService
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal val jsonPlaceholderApiModule: Module = module {
    single { JsonPlaceholderRetrofitFactory(androidContext()) }
    single { get<JsonPlaceholderRetrofitFactory>().createService(JsonPlaceholderPostsService::class.java) }
    single { get<JsonPlaceholderRetrofitFactory>().createService(JsonPlaceholderCommentsService::class.java) }
    single { get<JsonPlaceholderRetrofitFactory>().createService(JsonPlaceholderUsersService::class.java) }
    single { get<JsonPlaceholderRetrofitFactory>().createService(JsonPlaceholderAlbumsService::class.java) }
    single { get<JsonPlaceholderRetrofitFactory>().createService(JsonPlaceholderPhotosService::class.java) }
}
