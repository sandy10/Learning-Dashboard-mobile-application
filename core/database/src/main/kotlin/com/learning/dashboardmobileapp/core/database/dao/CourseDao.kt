package com.learning.dashboardmobileapp.core.database.dao

import com.learning.dashboardmobileapp.core.database.model.CourseEntity
import com.learning.dashboardmobileapp.core.database.model.CourseWithLessons
import com.learning.dashboardmobileapp.core.database.model.LessonEntity
import kotlinx.coroutines.flow.Flow

interface CourseDao {
    fun observeCourses(): Flow<List<CourseEntity>>
    fun observeCourseWithLessons(courseId: Long): Flow<CourseWithLessons?>
    suspend fun getCourseWithLessons(courseId: Long): CourseWithLessons?
    suspend fun getLesson(lessonId: Long): LessonEntity?
    suspend fun getLessonsForCourse(courseId: Long): List<LessonEntity>
    suspend fun insertCourses(courses: List<CourseEntity>)
    suspend fun insertLessons(lessons: List<LessonEntity>)
    suspend fun updateLessonCompleted(lessonId: Long, isCompleted: Boolean = true)
    suspend fun updateCourseProgress(courseId: Long, progress: Int)
    suspend fun upsertPreservingLocalCompletion(
        remoteCourses: List<CourseEntity>,
        remoteLessons: List<LessonEntity>
    )
}
