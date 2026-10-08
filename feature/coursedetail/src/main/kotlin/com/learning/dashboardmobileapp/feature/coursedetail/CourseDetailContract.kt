package com.learning.dashboardmobileapp.feature.coursedetail

import com.learning.dashboardmobileapp.core.domain.model.CourseDetails
import com.learning.dashboardmobileapp.core.domain.model.Lesson

sealed interface CourseDetailUiAction {
    data class OnToggleLessonExpanded(val lessonId: Long) : CourseDetailUiAction
    data class OnMarkLessonCompleted(val lessonId: Long) : CourseDetailUiAction
    data object OnNavigateBack : CourseDetailUiAction
    data object OnDismissSnackbar : CourseDetailUiAction
}

data class CourseDetailUiState(
    val isLoading: Boolean = true,
    val courseDetails: CourseDetails? = null,
    val expandedLessonId: Long? = null,
    val isMarkingComplete: Boolean = false,
    val errorMessage: String? = null
) {
    val completedLessonsCount: Int
        get() = courseDetails?.lessons?.count { it.isCompleted } ?: 0

    val totalLessonsCount: Int
        get() = courseDetails?.lessons?.size ?: 0
}

sealed interface CourseDetailUiEvent {
    data object NavigateBack : CourseDetailUiEvent
    data class ShowSnackbar(val message: String) : CourseDetailUiEvent
}
