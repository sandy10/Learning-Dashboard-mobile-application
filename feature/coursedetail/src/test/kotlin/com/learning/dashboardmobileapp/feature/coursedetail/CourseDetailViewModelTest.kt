package com.learning.dashboardmobileapp.feature.coursedetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.learning.dashboardmobileapp.core.domain.model.Course
import com.learning.dashboardmobileapp.core.domain.model.CourseDetails
import com.learning.dashboardmobileapp.core.domain.model.Lesson
import com.learning.dashboardmobileapp.core.domain.model.LessonType
import com.learning.dashboardmobileapp.core.domain.repository.CourseRepository
import com.learning.dashboardmobileapp.core.domain.usecase.MarkLessonCompletedUseCase
import com.learning.dashboardmobileapp.core.domain.usecase.ObserveCourseDetailsUseCase
import com.learning.dashboardmobileapp.core.domain.util.AppResult
import com.learning.dashboardmobileapp.core.domain.util.ProgressCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CourseDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeCourseRepository
    private lateinit var observeCourseDetailsUseCase: ObserveCourseDetailsUseCase
    private lateinit var markLessonCompletedUseCase: MarkLessonCompletedUseCase
    private lateinit var viewModel: CourseDetailViewModel

    private val initialLessons = listOf(
        Lesson(id = 101L, courseId = 1L, title = "1. Intro", durationMinutes = 10, type = LessonType.VIDEO, isCompleted = true),
        Lesson(id = 102L, courseId = 1L, title = "2. Basics", durationMinutes = 15, type = LessonType.CODE_LAB, isCompleted = true),
        Lesson(id = 103L, courseId = 1L, title = "3. Functions", durationMinutes = 20, type = LessonType.INTERACTIVE_PRACTICE, isCompleted = false)
    )

    private val initialCourse = Course(
        id = 1L,
        title = "Python Programming",
        instructor = "John Smith",
        progress = 67, // 2 of 3 completed ~ 67%
        lessonsCount = 3
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeCourseRepository(
            initialDetails = CourseDetails(course = initialCourse, lessons = initialLessons)
        )
        observeCourseDetailsUseCase = ObserveCourseDetailsUseCase(fakeRepository)
        markLessonCompletedUseCase = MarkLessonCompletedUseCase(fakeRepository)

        val savedStateHandle = SavedStateHandle(mapOf("courseId" to 1L))
        viewModel = CourseDetailViewModel(
            savedStateHandle = savedStateHandle,
            observeCourseDetailsUseCase = observeCourseDetailsUseCase,
            markLessonCompletedUseCase = markLessonCompletedUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialCourseDetails_loadedCorrectly() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.courseDetails)
        assertEquals("Python Programming", state.courseDetails?.course?.title)
        assertEquals(3, state.totalLessonsCount)
        assertEquals(2, state.completedLessonsCount)
    }

    @Test
    fun toggleLessonExpanded_expandsAndCollapses() {
        viewModel.onAction(CourseDetailUiAction.OnToggleLessonExpanded(103L))
        assertEquals(103L, viewModel.uiState.value.expandedLessonId)

        // Toggle same lesson collapses it
        viewModel.onAction(CourseDetailUiAction.OnToggleLessonExpanded(103L))
        assertEquals(null, viewModel.uiState.value.expandedLessonId)
    }

    @Test
    fun markLessonCompleted_updatesLessonStatusAndRecalculatesCourseProgressToOneHundred() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiEvent.test {
            // Expand pending lesson 103
            viewModel.onAction(CourseDetailUiAction.OnToggleLessonExpanded(103L))

            // Mark completed
            viewModel.onAction(CourseDetailUiAction.OnMarkLessonCompleted(103L))
            testDispatcher.scheduler.advanceUntilIdle()

            // Verify event emitted
            val event = awaitItem()
            assertTrue(event is CourseDetailUiEvent.ShowSnackbar)

            // Verify new state has 3 of 3 completed and 100% progress
            val state = viewModel.uiState.value
            assertEquals(3, state.completedLessonsCount)
            assertEquals(100, state.courseDetails?.course?.progress)
            assertEquals(null, state.expandedLessonId) // collapsed
            assertTrue(state.courseDetails?.lessons?.find { it.id == 103L }?.isCompleted == true)
        }
    }
}

class FakeCourseRepository(
    initialDetails: CourseDetails
) : CourseRepository {

    private val detailsFlow = MutableStateFlow(initialDetails)

    override fun observeCourses(): Flow<List<Course>> = MutableStateFlow(listOf(detailsFlow.value.course))

    override fun observeCourseDetails(courseId: Long): Flow<CourseDetails?> = detailsFlow

    override suspend fun refreshCourses(): AppResult<Unit> = AppResult.Success(Unit)

    override suspend fun markLessonCompleted(lessonId: Long): AppResult<Unit> {
        val current = detailsFlow.value
        val updatedLessons = current.lessons.map {
            if (it.id == lessonId) it.copy(isCompleted = true) else it
        }
        val completedCount = updatedLessons.count { it.isCompleted }
        val newProgress = ProgressCalculator.calculateProgress(completedCount, updatedLessons.size)
        val updatedCourse = current.course.copy(progress = newProgress)

        detailsFlow.value = CourseDetails(course = updatedCourse, lessons = updatedLessons)
        return AppResult.Success(Unit)
    }

    override suspend fun setSimulateOffline(simulate: Boolean) {}

    override fun observeSimulateOffline(): Flow<Boolean> = MutableStateFlow(false)
}
