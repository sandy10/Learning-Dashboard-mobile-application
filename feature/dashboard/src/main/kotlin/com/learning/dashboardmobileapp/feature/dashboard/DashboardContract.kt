package com.learning.dashboardmobileapp.feature.dashboard

import com.learning.dashboardmobileapp.core.domain.model.Course

enum class DashboardPreviewMode {
    NORMAL,
    SKELETON,
    EMPTY,
    ERROR
}

sealed interface DashboardUiAction {
    data class OnCourseClicked(val courseId: Long) : DashboardUiAction
    data object OnRefresh : DashboardUiAction
    data object OnToggleSimulateOffline : DashboardUiAction
    data class OnPreviewModeChanged(val mode: DashboardPreviewMode) : DashboardUiAction
    data object OnDismissError : DashboardUiAction
    data object OnLogout : DashboardUiAction
}

data class DashboardUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val courses: List<Course> = emptyList(),
    val errorMessage: String? = null,
    val isOnline: Boolean = true,
    val isSimulateOffline: Boolean = false,
    val previewMode: DashboardPreviewMode = DashboardPreviewMode.NORMAL
) {
    val totalEnrolled: Int get() = courses.size
}

sealed interface DashboardUiEvent {
    data class NavigateToCourseDetails(val courseId: Long) : DashboardUiEvent
    data object NavigateToLogin : DashboardUiEvent
    data class ShowSnackbar(val message: String) : DashboardUiEvent
}
