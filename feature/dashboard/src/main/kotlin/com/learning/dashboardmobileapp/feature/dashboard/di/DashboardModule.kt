package com.learning.dashboardmobileapp.feature.dashboard.di

import com.learning.dashboardmobileapp.feature.dashboard.DashboardViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val dashboardModule = module {
    viewModel { DashboardViewModel(get(), get(), get(), get()) }
}
