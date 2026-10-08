package com.learning.dashboardmobileapp

import android.app.Application
import com.learning.dashboardmobileapp.core.data.di.dataModule
import com.learning.dashboardmobileapp.core.database.di.databaseModule
import com.learning.dashboardmobileapp.core.network.di.networkModule
import com.learning.dashboardmobileapp.feature.auth.di.authModule
import com.learning.dashboardmobileapp.feature.coursedetail.di.courseDetailModule
import com.learning.dashboardmobileapp.feature.dashboard.di.dashboardModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class LearningApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@LearningApplication)
            modules(
                networkModule,
                databaseModule,
                dataModule,
                authModule,
                dashboardModule,
                courseDetailModule
            )
        }
    }
}
