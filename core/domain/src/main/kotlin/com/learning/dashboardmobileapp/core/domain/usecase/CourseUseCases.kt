package com.learning.dashboardmobileapp.core.domain.usecase

import com.learning.dashboardmobileapp.core.domain.model.Course
import com.learning.dashboardmobileapp.core.domain.model.CourseDetails
import com.learning.dashboardmobileapp.core.domain.repository.CourseRepository
import com.learning.dashboardmobileapp.core.domain.util.AppResult
import kotlinx.coroutines.flow.Flow

class ObserveCoursesUseCase(
    private val repository: CourseRepository
) {
    operator fun invoke(): Flow<List<Course>> = repository.observeCourses()
}

class RefreshCoursesUseCase(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(): AppResult<Unit> = repository.refreshCourses()
}

class ObserveCourseDetailsUseCase(
    private val repository: CourseRepository
) {
    operator fun invoke(courseId: Long): Flow<CourseDetails?> = repository.observeCourseDetails(courseId)
}

class MarkLessonCompletedUseCase(
    private val repository: CourseRepository
) {
    suspend operator fun invoke(lessonId: Long): AppResult<Unit> = repository.markLessonCompleted(lessonId)
}

class ToggleSimulateOfflineUseCase(
    private val repository: CourseRepository
) {
    suspend fun setSimulate(simulate: Boolean) = repository.setSimulateOffline(simulate)
    fun observe(): Flow<Boolean> = repository.observeSimulateOffline()
}
