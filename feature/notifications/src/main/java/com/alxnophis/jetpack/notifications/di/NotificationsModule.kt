package com.alxnophis.jetpack.notifications.di

import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

fun injectNotifications() = loadNotificationsModules

private val loadNotificationsModules by lazy {
    loadKoinModules(
        listOf(notificationsModule)
    )
}

private val notificationsModule: Module = module {}
