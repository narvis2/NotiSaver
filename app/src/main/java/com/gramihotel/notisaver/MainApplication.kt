package com.gramihotel.notisaver

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.gramihotel.notisaver.service.NotificationListener
import com.gramihotel.notisaver.utils.TimberDebugTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO).build()

    override fun onCreate() {
        super.onCreate()
        NotificationListener.tryReEnableNotificationListener(applicationContext)

        if (BuildConfig.DEBUG) {
            Timber.plant(TimberDebugTree())
        }
    }
}