package com.learning.dashboardmobileapp.core.network.di

import com.learning.dashboardmobileapp.core.network.api.AuthApi
import com.learning.dashboardmobileapp.core.network.api.CourseApi
import com.learning.dashboardmobileapp.core.network.api.MockAuthApi
import com.learning.dashboardmobileapp.core.network.api.MockCourseApi
import com.learning.dashboardmobileapp.core.network.monitor.ConnectivityManagerNetworkMonitor
import com.learning.dashboardmobileapp.core.network.monitor.NetworkMonitor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val networkModule = module {
    single<NetworkMonitor> { ConnectivityManagerNetworkMonitor(androidContext()) }
    single<CourseApi> { MockCourseApi(get()) }
    single<AuthApi> { MockAuthApi(get(), get()) }
}
