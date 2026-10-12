package com.alxnophis.jetpack.core.base.application

import android.app.Application
import android.app.NotificationManager
import android.os.StrictMode
import com.alxnophis.jetpack.core.BuildConfig
import com.alxnophis.jetpack.core.base.provider.NotificationChannelProvider
import com.alxnophis.jetpack.core.di.KoinLogger
import com.alxnophis.jetpack.core.di.coreModules
import com.alxnophis.jetpack.core.extensions.isDebugBuildType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import timber.log.Timber

open class BaseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
        initLogger()
        initNotificationChannels()
        initStrictMode()
    }

    protected open fun getFeatureModules(): List<Module> = emptyList()

    protected open fun getCoreModules(): List<Module> = coreModules

    private fun initKoin() {
        startKoin {
            androidContext(this@BaseApp)
            if (BuildConfig.DEBUG) {
                logger(KoinLogger(Level.ERROR))
            }
            modules(
                getCoreModules() + getFeatureModules(),
            )
        }
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initNotificationChannels() {
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            NotificationChannelProvider(this@BaseApp).apply {
                createNotificationChannel(
                    channelId = NotificationChannelProvider.DEFAULT_NOTIFICATION_CHANNEL_ID,
                    channelName = NotificationChannelProvider.DEFAULT_NOTIFICATION_CHANNEL_NAME,
                    notificationImportance = NotificationManager.IMPORTANCE_HIGH,
                )
            }
        }
    }

    private fun initStrictMode() {
        if (isDebugBuildType()) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy
                    .Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork() // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build(),
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy
                    .Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
//                .penaltyDeath()
                    .build(),
            )
        }
    }
}
