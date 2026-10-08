package com.learning.dashboardmobileapp.core.database.di

import com.learning.dashboardmobileapp.core.database.dao.CourseDao
import com.learning.dashboardmobileapp.core.database.sqlite.LearningDbHelper
import com.learning.dashboardmobileapp.core.database.sqlite.SQLiteCourseDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { LearningDbHelper(androidContext()) }
    single<CourseDao> { SQLiteCourseDao(get()) }
}
