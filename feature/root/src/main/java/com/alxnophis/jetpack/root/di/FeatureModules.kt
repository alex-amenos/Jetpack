package com.alxnophis.jetpack.root.di

import com.alxnophis.jetpack.authentication.di.authenticationModule
import com.alxnophis.jetpack.filedownloader.di.fileDownloaderModule
import com.alxnophis.jetpack.game.ballclicker.di.ballClickerModule
import com.alxnophis.jetpack.home.di.homeModule
import com.alxnophis.jetpack.location.tracker.di.locationTrackerModule
import com.alxnophis.jetpack.myplayground.di.myPlaygroundModule
import com.alxnophis.jetpack.notifications.di.notificationsModule
import com.alxnophis.jetpack.posts.di.postsModule
import com.alxnophis.jetpack.settings.di.settingsModule
import org.koin.core.module.Module

val featureModules: List<Module> =
    listOf(
        authenticationModule,
        ballClickerModule,
        fileDownloaderModule,
        homeModule,
        locationTrackerModule,
        myPlaygroundModule,
        notificationsModule,
        postsModule,
        settingsModule,
    )
