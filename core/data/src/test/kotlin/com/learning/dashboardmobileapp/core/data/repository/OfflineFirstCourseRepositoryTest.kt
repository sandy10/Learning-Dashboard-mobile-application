package com.learning.dashboardmobileapp.core.data.repository

import app.cash.turbine.test
import com.learning.dashboardmobileapp.core.database.dao.CourseDao
import com.learning.dashboardmobileapp.core.database.model.CourseEntity
import com.learning.dashboardmobileapp.core.database.model.CourseWithLessons
import com.learning.dashboardmobileapp.core.database.model.LessonEntity
import com.learning.dashboardmobileapp.core.domain.util.AppResult
import com.learning.dashboardmobileapp.core.network.api.CourseApi
import com.learning.dashboardmobileapp.core.network.model.CourseDto
import com.learning.dashboardmobileapp.core.network.model.LessonDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class OfflineFirstCourseRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDao: FakeCourseDao
    private lateinit var fakeApi: FakeCourseApi
    private lateinit var repository: OfflineFirstCourseRepository

    @Before
    fun setUp() {
        fakeDao = FakeCourseDao()
        fakeApi = FakeCourseApi()
        repository = OfflineFirstCourseRepository(
            courseDao = fakeDao,
            courseApi = fakeApi,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun observeCourses_emitsDirectlyFromLocalDatabase() = runTest(testDispatcher) {
        val initialEntity = CourseEntity(
            id = 1L,
            title = "Python Programming",
            instructor = "John Smith",
            progress = 65,
            lessonsCount = 20,
            category = "Computer Science",
            isCertified = true,
            totalDurationHours = 4.5,
            quizScorePercent = 92
        )
        fakeDao.coursesFlow.value = listOf(initialEntity)

        repository.observeCourses().test {
            val courses = awaitItem()
            assertEquals(1, courses.size)
            assertEquals("Python Programming", courses[0].title)
            assertEquals(65, courses[0].progress)
        }
    }

    @Test
    fun refreshCourses_whenNetworkFails_returnsErrorButCachedDataRemainsAccessible() = runTest(testDispatcher) {
        // Pre-populate cache
        val cachedEntity = CourseEntity(
            id = 1L,
            title = "Python Programming",
            instructor = "John Smith",
            progress = 65,
            lessonsCount = 20,
            category = "Computer Science",
            isCertified = true,
            totalDurationHours = 4.5,
            quizScorePercent = 92
        )
        fakeDao.coursesFlow.value = listOf(cachedEntity)

        // Simulate network failure
        fakeApi.shouldThrow = true

        val result = repository.refreshCourses()

        assertTrue(result.isError())

        // Verify cached course is still in local database and accessible!
        repository.observeCourses().test {
            val courses = awaitItem()
            assertEquals(1, courses.size)
            assertEquals("Python Programming", courses[0].title)
        }
    }

    @Test
    fun markLessonCompleted_updatesRoomAndRecalculatesProgress() = runTest(testDispatcher) {
        // Set up a course with 2 lessons (1 completed, 1 pending = 50%)
        val lesson1 = LessonEntity(id = 101L, courseId = 1L, title = "L1", durationMinutes = 10, type = "VIDEO", description = "", isCompleted = true)
        val lesson2 = LessonEntity(id = 102L, courseId = 1L, title = "L2", durationMinutes = 10, type = "VIDEO", description = "", isCompleted = false)
        fakeDao.lessonsMap[101L] = lesson1
        fakeDao.lessonsMap[102L] = lesson2
        fakeDao.courseLessonsMap[1L] = mutableListOf(lesson1, lesson2)

        val result = repository.markLessonCompleted(102L)
        assertTrue(result.isSuccess())

        // Both lessons are now completed, so progress should be 100%
        assertEquals(true, fakeDao.lessonsMap[102L]?.isCompleted)
        assertEquals(100, fakeDao.updatedCourseProgress[1L])
    }
}

class FakeCourseDao : CourseDao {
    val coursesFlow = MutableStateFlow<List<CourseEntity>>(emptyList())
    val lessonsMap = mutableMapOf<Long, LessonEntity>()
    val courseLessonsMap = mutableMapOf<Long, MutableList<LessonEntity>>()
    val updatedCourseProgress = mutableMapOf<Long, Int>()

    override fun observeCourses(): Flow<List<CourseEntity>> = coursesFlow

    override fun observeCourseWithLessons(courseId: Long): Flow<CourseWithLessons?> {
        val course = coursesFlow.value.find { it.id == courseId }
        val lessons = courseLessonsMap[courseId] ?: emptyList()
        return MutableStateFlow(course?.let { CourseWithLessons(it, lessons) })
    }

    override suspend fun getCourseWithLessons(courseId: Long): CourseWithLessons? {
        val course = coursesFlow.value.find { it.id == courseId } ?: return null
        return CourseWithLessons(course, courseLessonsMap[courseId] ?: emptyList())
    }

    override suspend fun getLesson(lessonId: Long): LessonEntity? = lessonsMap[lessonId]

    override suspend fun getLessonsForCourse(courseId: Long): List<LessonEntity> =
        courseLessonsMap[courseId] ?: emptyList()

    override suspend fun insertCourses(courses: List<CourseEntity>) {
        coursesFlow.value = courses
    }

    override suspend fun insertLessons(lessons: List<LessonEntity>) {
        lessons.forEach {
            lessonsMap[it.id] = it
            courseLessonsMap.getOrPut(it.courseId) { mutableListOf() }.add(it)
        }
    }

    override suspend fun updateLessonCompleted(lessonId: Long, isCompleted: Boolean) {
        val existing = lessonsMap[lessonId]
        if (existing != null) {
            val updated = existing.copy(isCompleted = isCompleted)
            lessonsMap[lessonId] = updated
            courseLessonsMap[existing.courseId]?.let { list ->
                val index = list.indexOfFirst { it.id == lessonId }
                if (index != -1) list[index] = updated
            }
        }
    }

    override suspend fun updateCourseProgress(courseId: Long, progress: Int) {
        updatedCourseProgress[courseId] = progress
    }

    override suspend fun upsertPreservingLocalCompletion(
        remoteCourses: List<CourseEntity>,
        remoteLessons: List<LessonEntity>
    ) {
        coursesFlow.value = remoteCourses
        remoteLessons.forEach { lesson ->
            val existing = lessonsMap[lesson.id]
            val isComp = existing?.isCompleted ?: lesson.isCompleted
            val finalLesson = lesson.copy(isCompleted = isComp)
            lessonsMap[lesson.id] = finalLesson
            val list = courseLessonsMap.getOrPut(lesson.courseId) { mutableListOf() }
            val idx = list.indexOfFirst { it.id == lesson.id }
            if (idx != -1) list[idx] = finalLesson else list.add(finalLesson)
        }
    }
}

class FakeCourseApi : CourseApi {
    var shouldThrow = false
    private val _simOffline = MutableStateFlow(false)
    override val simulateOfflineFlow: StateFlow<Boolean> = _simOffline

    override fun setSimulateOffline(simulate: Boolean) {
        _simOffline.value = simulate
    }

    override suspend fun fetchCourses(): List<CourseDto> {
        if (shouldThrow || _simOffline.value) {
            throw IOException("Simulated network outage")
        }
        return emptyList()
    }
}
