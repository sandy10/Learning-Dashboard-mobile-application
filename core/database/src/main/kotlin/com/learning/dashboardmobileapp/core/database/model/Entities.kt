package com.learning.dashboardmobileapp.core.database.model

data class CourseEntity(
    val id: Long,
    val title: String,
    val instructor: String,
    val progress: Int,
    val lessonsCount: Int,
    val category: String = "Computer Science",
    val isCertified: Boolean = true,
    val totalDurationHours: Double = 4.5,
    val quizScorePercent: Int = 92,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class LessonEntity(
    val id: Long,
    val courseId: Long,
    val title: String,
    val durationMinutes: Int,
    val type: String,
    val description: String,
    val isCompleted: Boolean
)

data class CourseWithLessons(
    val course: CourseEntity,
    val lessons: List<LessonEntity>
)
