package com.learning.dashboardmobileapp.core.data.di

import com.learning.dashboardmobileapp.core.data.repository.DefaultAuthRepository
import com.learning.dashboardmobileapp.core.data.repository.OfflineFirstCourseRepository
import com.learning.dashboardmobileapp.core.data.security.CryptoManager
import com.learning.dashboardmobileapp.core.data.security.KeystoreCryptoManager
import com.learning.dashboardmobileapp.core.data.session.DataStoreSessionManager
import com.learning.dashboardmobileapp.core.data.session.SessionManager
import com.learning.dashboardmobileapp.core.domain.repository.AuthRepository
import com.learning.dashboardmobileapp.core.domain.repository.CourseRepository
import com.learning.dashboardmobileapp.core.domain.usecase.LoginUseCase
import com.learning.dashboardmobileapp.core.domain.usecase.LogoutUseCase
import com.learning.dashboardmobileapp.core.domain.usecase.MarkLessonCompletedUseCase
import com.learning.dashboardmobileapp.core.domain.usecase.ObserveAuthStateUseCase
import com.learning.dashboardmobileapp.core.domain.usecase.ObserveCourseDetailsUseCase
import com.learning.dashboardmobileapp.core.domain.usecase.ObserveCoursesUseCase
import com.learning.dashboardmobileapp.core.domain.usecase.RefreshCoursesUseCase
import com.learning.dashboardmobileapp.core.domain.usecase.ToggleSimulateOfflineUseCase
import com.learning.dashboardmobileapp.core.domain.util.EmailValidator
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<CryptoManager> { KeystoreCryptoManager() }
    single<SessionManager> { DataStoreSessionManager(androidContext(), get()) }
    single { EmailValidator() }

    single<CourseRepository> {
        OfflineFirstCourseRepository(
            courseDao = get(),
            courseApi = get(),
            ioDispatcher = Dispatchers.IO
        )
    }

    single<AuthRepository> {
        DefaultAuthRepository(
            authApi = get(),
            sessionManager = get(),
            ioDispatcher = Dispatchers.IO
        )
    }

    // Use cases
    factory { ObserveCoursesUseCase(get()) }
    factory { RefreshCoursesUseCase(get()) }
    factory { ObserveCourseDetailsUseCase(get()) }
    factory { MarkLessonCompletedUseCase(get()) }
    factory { ToggleSimulateOfflineUseCase(get()) }

    factory { LoginUseCase(get(), get()) }
    factory { LogoutUseCase(get()) }
    factory { ObserveAuthStateUseCase(get()) }
}
