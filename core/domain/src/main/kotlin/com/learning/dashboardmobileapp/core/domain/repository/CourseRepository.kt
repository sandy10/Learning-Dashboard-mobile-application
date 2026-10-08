package com.learning.dashboardmobileapp.core.domain.repository

import com.learning.dashboardmobileapp.core.domain.model.Course
import com.learning.dashboardmobileapp.core.domain.model.CourseDetails
import com.learning.dashboardmobileapp.core.domain.model.Lesson
import com.learning.dashboardmobileapp.core.domain.util.AppResult
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    fun observeCourses(): Flow<List<Course>>
    fun observeCourseDetails(courseId: Long): Flow<CourseDetails?>
    suspend fun refreshCourses(): AppResult<Unit>
    suspend fun markLessonCompleted(lessonId: Long): AppResult<Unit>
    suspend fun setSimulateOffline(simulate: Boolean)
    fun observeSimulateOffline(): Flow<Boolean>
}
