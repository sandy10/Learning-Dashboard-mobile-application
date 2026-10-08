package com.learning.dashboardmobileapp.core.data.mapper

import com.learning.dashboardmobileapp.core.database.model.CourseEntity
import com.learning.dashboardmobileapp.core.database.model.CourseWithLessons
import com.learning.dashboardmobileapp.core.database.model.LessonEntity
import com.learning.dashboardmobileapp.core.domain.model.Course
import com.learning.dashboardmobileapp.core.domain.model.CourseDetails
import com.learning.dashboardmobileapp.core.domain.model.Lesson
import com.learning.dashboardmobileapp.core.domain.model.LessonType
import com.learning.dashboardmobileapp.core.domain.model.User
import com.learning.dashboardmobileapp.core.network.model.CourseDto
import com.learning.dashboardmobileapp.core.network.model.LessonDto
import com.learning.dashboardmobileapp.core.network.model.LoginResponseDto

fun CourseDto.toEntity(): CourseEntity = CourseEntity(
    id = id,
    title = title,
    instructor = instructor,
    progress = progress,
    lessonsCount = lessons,
    category = category,
    isCertified = isCertified,
    totalDurationHours = totalDurationHours,
    quizScorePercent = quizScorePercent
)

fun LessonDto.toEntity(courseId: Long): LessonEntity = LessonEntity(
    id = id,
    courseId = courseId,
    title = title,
    durationMinutes = durationMinutes,
    type = type,
    description = description,
    isCompleted = isCompleted
)

fun CourseEntity.toDomain(): Course = Course(
    id = id,
    title = title,
    instructor = instructor,
    progress = progress,
    lessonsCount = lessonsCount,
    category = category,
    isCertified = isCertified,
    totalDurationHours = totalDurationHours,
    quizScorePercent = quizScorePercent
)

fun LessonEntity.toDomain(): Lesson {
    val parsedType = try {
        LessonType.valueOf(type)
    } catch (_: Exception) {
        LessonType.VIDEO
    }
    return Lesson(
        id = id,
        courseId = courseId,
        title = title,
        durationMinutes = durationMinutes,
        type = parsedType,
        description = description,
        isCompleted = isCompleted
    )
}

fun CourseWithLessons.toDomain(): CourseDetails = CourseDetails(
    course = course.toDomain(),
    lessons = lessons.map { it.toDomain() }
)

fun LoginResponseDto.toDomainUser(): User = User(
    id = userId,
    email = email,
    name = name
)
