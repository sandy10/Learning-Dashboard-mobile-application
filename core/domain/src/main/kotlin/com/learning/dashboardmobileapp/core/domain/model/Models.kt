package com.learning.dashboardmobileapp.core.domain.model

enum class LessonType(val displayName: String) {
    VIDEO("Video Lesson"),
    CODE_LAB("Code Lab"),
    INTERACTIVE_PRACTICE("Interactive Practice"),
    DEEP_DIVE("Deep Dive"),
    PRACTICE_EXERCISE("Practice Exercise")
}

data class Lesson(
    val id: Long,
    val courseId: Long,
    val title: String,
    val durationMinutes: Int = 15,
    val type: LessonType = LessonType.VIDEO,
    val description: String = "",
    val isCompleted: Boolean = false
)

data class Course(
    val id: Long,
    val title: String,
    val instructor: String,
    val progress: Int,
    val lessonsCount: Int,
    val category: String = "Computer Science",
    val isCertified: Boolean = true,
    val totalDurationHours: Double = 4.5,
    val quizScorePercent: Int = 92
)

data class CourseDetails(
    val course: Course,
    val lessons: List<Lesson>
)

data class User(
    val id: String,
    val email: String,
    val name: String
)
