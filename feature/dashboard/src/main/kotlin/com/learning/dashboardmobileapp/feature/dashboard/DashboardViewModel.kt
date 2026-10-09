package com.learning.dashboardmobileapp.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.dashboardmobileapp.core.domain.usecase.LogoutUseCase
import com.learning.dashboardmobileapp.core.domain.usecase.ObserveCoursesUseCase
import com.learning.dashboardmobileapp.core.domain.usecase.RefreshCoursesUseCase
import com.learning.dashboardmobileapp.core.domain.usecase.ToggleSimulateOfflineUseCase
import com.learning.dashboardmobileapp.core.domain.util.CourseConstants
import com.learning.dashboardmobileapp.core.domain.util.onError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val observeCoursesUseCase: ObserveCoursesUseCase,
    private val refreshCoursesUseCase: RefreshCoursesUseCase,
    private val toggleSimulateOfflineUseCase: ToggleSimulateOfflineUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<DashboardUiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        observeCourses()
        observeSimulateOffline()
        refreshCourses()
    }

    private fun observeCourses() {
        viewModelScope.launch {
            observeCoursesUseCase().collect { courseList ->
                _uiState.update {
                    it.copy(
                        courses = courseList,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun observeSimulateOffline() {
        viewModelScope.launch {
            toggleSimulateOfflineUseCase.observe().collect { isSimulated ->
                _uiState.update { it.copy(isSimulateOffline = isSimulated) }
            }
        }
    }

    fun onAction(action: DashboardUiAction) {
        when (action) {
            is DashboardUiAction.OnCourseClicked -> {
                viewModelScope.launch {
                    _uiEvent.send(DashboardUiEvent.NavigateToCourseDetails(action.courseId))
                }
            }

            DashboardUiAction.OnRefresh -> {
                refreshCourses()
            }

            DashboardUiAction.OnToggleSimulateOffline -> {
                viewModelScope.launch {
                    val current = _uiState.value.isSimulateOffline
                    toggleSimulateOfflineUseCase.setSimulate(!current)
                }
            }

            is DashboardUiAction.OnPreviewModeChanged -> {
                _uiState.update { it.copy(previewMode = action.mode) }
            }

            DashboardUiAction.OnDismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }

            DashboardUiAction.OnLogout -> {
                viewModelScope.launch {
                    logoutUseCase()
                    _uiEvent.send(DashboardUiEvent.NavigateToLogin)
                }
            }
        }
    }

    private fun refreshCourses() {
        viewModelScope.launch {
            val hasCachedData = _uiState.value.courses.isNotEmpty()
            _uiState.update {
                it.copy(
                    isRefreshing = true,
                    isLoading = !hasCachedData,
                    errorMessage = null
                )
            }

            val result = refreshCoursesUseCase()
            result.onError { error ->
                _uiState.update {
                    it.copy(
                        errorMessage = if (hasCachedData) {
                            CourseConstants.ERROR_COURSES_OFFLINE
                        } else {
                            CourseConstants.ERROR_COURSES_NETWORK
                        }
                    )
                }
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false
                )
            }
        }
    }
}
