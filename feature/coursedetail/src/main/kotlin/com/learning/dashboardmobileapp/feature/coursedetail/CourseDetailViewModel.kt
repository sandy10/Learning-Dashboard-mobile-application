package com.learning.dashboardmobileapp.feature.coursedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.dashboardmobileapp.core.domain.usecase.MarkLessonCompletedUseCase
import com.learning.dashboardmobileapp.core.domain.usecase.ObserveCourseDetailsUseCase
import com.learning.dashboardmobileapp.core.domain.util.AppResult
import com.learning.dashboardmobileapp.core.domain.util.CourseConstants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CourseDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val observeCourseDetailsUseCase: ObserveCourseDetailsUseCase,
    private val markLessonCompletedUseCase: MarkLessonCompletedUseCase
) : ViewModel() {

    private val courseId: Long = checkNotNull(savedStateHandle.get<Long>("courseId")) {
        CourseConstants.ERROR_COURSE_ID_REQUIRED
    }

    private val _uiState = MutableStateFlow(CourseDetailUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<CourseDetailUiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        observeCourseDetails()
    }

    private fun observeCourseDetails() {
        viewModelScope.launch {
            observeCourseDetailsUseCase(courseId).collect { details ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        courseDetails = details
                    )
                }
            }
        }
    }

    fun onAction(action: CourseDetailUiAction) {
        when (action) {
            is CourseDetailUiAction.OnToggleLessonExpanded -> {
                _uiState.update {
                    val newId = if (it.expandedLessonId == action.lessonId) null else action.lessonId
                    it.copy(expandedLessonId = newId)
                }
            }

            is CourseDetailUiAction.OnMarkLessonCompleted -> {
                markLessonCompleted(action.lessonId)
            }

            CourseDetailUiAction.OnNavigateBack -> {
                viewModelScope.launch {
                    _uiEvent.send(CourseDetailUiEvent.NavigateBack)
                }
            }

            CourseDetailUiAction.OnDismissSnackbar -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun markLessonCompleted(lessonId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isMarkingComplete = true) }

            when (markLessonCompletedUseCase(lessonId)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isMarkingComplete = false,
                            expandedLessonId = null // collapse upon completion
                        )
                    }
                    _uiEvent.send(CourseDetailUiEvent.ShowSnackbar(CourseConstants.MSG_LESSON_COMPLETED_SUCCESS))
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isMarkingComplete = false,
                            errorMessage = CourseConstants.ERROR_COULD_NOT_UPDATE_LESSON
                        )
                    }
                }
            }
        }
    }
}
