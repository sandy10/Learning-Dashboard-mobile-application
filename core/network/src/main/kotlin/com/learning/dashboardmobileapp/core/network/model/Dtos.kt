package com.learning.dashboardmobileapp.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LessonDto(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("durationMinutes") val durationMinutes: Int = 15,
    @SerialName("type") val type: String = "VIDEO",
    @SerialName("description") val description: String = "",
    @SerialName("isCompleted") val isCompleted: Boolean = false
)

@Serializable
data class CourseDto(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("instructor") val instructor: String,
    @SerialName("progress") val progress: Int,
    @SerialName("lessons") val lessons: Int,
    @SerialName("category") val category: String = "Computer Science",
    @SerialName("isCertified") val isCertified: Boolean = true,
    @SerialName("totalDurationHours") val totalDurationHours: Double = 4.5,
    @SerialName("quizScorePercent") val quizScorePercent: Int = 92,
    @SerialName("lessonList") val lessonList: List<LessonDto> = emptyList()
)

@Serializable
data class LoginRequestDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String
)

@Serializable
data class LoginResponseDto(
    @SerialName("token") val token: String,
    @SerialName("userId") val userId: String,
    @SerialName("email") val email: String,
    @SerialName("name") val name: String
)
