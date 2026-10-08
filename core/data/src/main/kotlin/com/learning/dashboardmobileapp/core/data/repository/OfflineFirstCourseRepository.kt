package com.learning.dashboardmobileapp.core.data.repository

import com.learning.dashboardmobileapp.core.data.mapper.toDomain
import com.learning.dashboardmobileapp.core.data.mapper.toEntity
import com.learning.dashboardmobileapp.core.database.dao.CourseDao
import com.learning.dashboardmobileapp.core.domain.model.Course
import com.learning.dashboardmobileapp.core.domain.model.CourseDetails
import com.learning.dashboardmobileapp.core.domain.repository.CourseRepository
import com.learning.dashboardmobileapp.core.domain.util.AppError
import com.learning.dashboardmobileapp.core.domain.util.AppResult
import com.learning.dashboardmobileapp.core.domain.util.ProgressCalculator
import com.learning.dashboardmobileapp.core.network.api.CourseApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

class OfflineFirstCourseRepository(
    private val courseDao: CourseDao,
    private val courseApi: CourseApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CourseRepository {

    override fun observeCourses(): Flow<List<Course>> {
        return courseDao.observeCourses().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeCourseDetails(courseId: Long): Flow<CourseDetails?> {
        return courseDao.observeCourseWithLessons(courseId).map { withLessons ->
            withLessons?.toDomain()
        }
    }

    override suspend fun refreshCourses(): AppResult<Unit> = withContext(ioDispatcher) {
        try {
            val remoteCourses = courseApi.fetchCourses()
            val courseEntities = remoteCourses.map { it.toEntity() }
            val lessonEntities = remoteCourses.flatMap { courseDto ->
                courseDto.lessonList.map { it.toEntity(courseDto.id) }
            }

            // Sync with Room using our conflict-resolution transaction that preserves local completion
            courseDao.upsertPreservingLocalCompletion(courseEntities, lessonEntities)
            AppResult.Success(Unit)
        } catch (e: IOException) {
            AppResult.Error(AppError.Network(e.message ?: "Failed to connect to server"))
        } catch (e: Exception) {
            AppResult.Error(AppError.Unknown(e, e.message ?: "Unexpected error occurred"))
        }
    }

    override suspend fun markLessonCompleted(lessonId: Long): AppResult<Unit> = withContext(ioDispatcher) {
        try {
            val lesson = courseDao.getLesson(lessonId)
                ?: return@withContext AppResult.Error(AppError.NotFound)

            // Mark lesson completed in Room
            courseDao.updateLessonCompleted(lessonId, isCompleted = true)

            // Recalculate course progress
            val allLessons = courseDao.getLessonsForCourse(lesson.courseId)
            val completedCount = allLessons.count { it.isCompleted || it.id == lessonId }
            val totalCount = allLessons.size
            val newProgress = ProgressCalculator.calculateProgress(completedCount, totalCount)

            courseDao.updateCourseProgress(lesson.courseId, newProgress)
            AppResult.Success(Unit)
        } catch (e: Exception) {
            AppResult.Error(AppError.Unknown(e, e.message ?: "Failed to update lesson"))
        }
    }

    override suspend fun setSimulateOffline(simulate: Boolean) {
        courseApi.setSimulateOffline(simulate)
    }

    override fun observeSimulateOffline(): Flow<Boolean> {
        return courseApi.simulateOfflineFlow
    }
}
