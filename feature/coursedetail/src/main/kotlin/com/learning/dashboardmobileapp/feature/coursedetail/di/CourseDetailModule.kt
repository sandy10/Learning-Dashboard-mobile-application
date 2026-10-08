package com.learning.dashboardmobileapp.feature.coursedetail.di

import com.learning.dashboardmobileapp.feature.coursedetail.CourseDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val courseDetailModule = module {
    viewModel { CourseDetailViewModel(get(), get(), get()) }
}
