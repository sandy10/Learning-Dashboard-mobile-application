package com.learning.dashboardmobileapp.feature.auth.di

import com.learning.dashboardmobileapp.feature.auth.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    viewModel { LoginViewModel(get(), get()) }
}
