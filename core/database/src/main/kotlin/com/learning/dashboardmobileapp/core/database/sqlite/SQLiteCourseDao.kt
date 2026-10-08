package com.learning.dashboardmobileapp.core.database.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.learning.dashboardmobileapp.core.database.dao.CourseDao
import com.learning.dashboardmobileapp.core.database.model.CourseEntity
import com.learning.dashboardmobileapp.core.database.model.CourseWithLessons
import com.learning.dashboardmobileapp.core.database.model.LessonEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class LearningDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "learning_dashboard.db"
        const val DATABASE_VERSION = 1

        const val TABLE_COURSES = "courses"
        const val COL_COURSE_ID = "id"
        const val COL_COURSE_TITLE = "title"
        const val COL_COURSE_INSTRUCTOR = "instructor"
        const val COL_COURSE_PROGRESS = "progress"
        const val COL_COURSE_LESSONS_COUNT = "lessonsCount"
        const val COL_COURSE_CATEGORY = "category"
        const val COL_COURSE_IS_CERTIFIED = "isCertified"
        const val COL_COURSE_DURATION = "totalDurationHours"
        const val COL_COURSE_QUIZ_SCORE = "quizScorePercent"
        const val COL_COURSE_LAST_UPDATED = "lastUpdated"

        const val TABLE_LESSONS = "lessons"
        const val COL_LESSON_ID = "id"
        const val COL_LESSON_COURSE_ID = "courseId"
        const val COL_LESSON_TITLE = "title"
        const val COL_LESSON_DURATION = "durationMinutes"
        const val COL_LESSON_TYPE = "type"
        const val COL_LESSON_DESCRIPTION = "description"
        const val COL_LESSON_IS_COMPLETED = "isCompleted"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_COURSES (
                $COL_COURSE_ID INTEGER PRIMARY KEY,
                $COL_COURSE_TITLE TEXT NOT NULL,
                $COL_COURSE_INSTRUCTOR TEXT NOT NULL,
                $COL_COURSE_PROGRESS INTEGER NOT NULL,
                $COL_COURSE_LESSONS_COUNT INTEGER NOT NULL,
                $COL_COURSE_CATEGORY TEXT NOT NULL,
                $COL_COURSE_IS_CERTIFIED INTEGER NOT NULL,
                $COL_COURSE_DURATION REAL NOT NULL,
                $COL_COURSE_QUIZ_SCORE INTEGER NOT NULL,
                $COL_COURSE_LAST_UPDATED INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_LESSONS (
                $COL_LESSON_ID INTEGER PRIMARY KEY,
                $COL_LESSON_COURSE_ID INTEGER NOT NULL,
                $COL_LESSON_TITLE TEXT NOT NULL,
                $COL_LESSON_DURATION INTEGER NOT NULL,
                $COL_LESSON_TYPE TEXT NOT NULL,
                $COL_LESSON_DESCRIPTION TEXT NOT NULL,
                $COL_LESSON_IS_COMPLETED INTEGER NOT NULL,
                FOREIGN KEY($COL_LESSON_COURSE_ID) REFERENCES $TABLE_COURSES($COL_COURSE_ID) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_lessons_course_id ON $TABLE_LESSONS($COL_LESSON_COURSE_ID)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LESSONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COURSES")
        onCreate(db)
    }
}

class SQLiteCourseDao(
    private val dbHelper: LearningDbHelper
) : CourseDao {

    private val mutex = Mutex()
    private val _changeTrigger = MutableStateFlow(0L)

    private fun notifyChanged() {
        _changeTrigger.value = System.currentTimeMillis()
    }

    override fun observeCourses(): Flow<List<CourseEntity>> = kotlinx.coroutines.flow.flow {
        _changeTrigger.collect {
            emit(getAllCourses())
        }
    }

    override fun observeCourseWithLessons(courseId: Long): Flow<CourseWithLessons?> = kotlinx.coroutines.flow.flow {
        _changeTrigger.collect {
            emit(getCourseWithLessons(courseId))
        }
    }

    private suspend fun getAllCourses(): List<CourseEntity> = withContext(Dispatchers.IO) {
        mutex.withLock {
            val db = dbHelper.readableDatabase
            val cursor = db.query(
                LearningDbHelper.TABLE_COURSES,
                null, null, null, null, null,
                "${LearningDbHelper.COL_COURSE_ID} ASC"
            )
            val list = mutableListOf<CourseEntity>()
            cursor.use {
                while (it.moveToNext()) {
                    list.add(it.readCourseEntity())
                }
            }
            list
        }
    }

    override suspend fun getCourseWithLessons(courseId: Long): CourseWithLessons? = withContext(Dispatchers.IO) {
        mutex.withLock {
            val db = dbHelper.readableDatabase
            val courseCursor = db.query(
                LearningDbHelper.TABLE_COURSES,
                null,
                "${LearningDbHelper.COL_COURSE_ID} = ?",
                arrayOf(courseId.toString()),
                null, null, null
            )
            val course = courseCursor.use {
                if (it.moveToNext()) it.readCourseEntity() else null
            } ?: return@withLock null

            val lessonsCursor = db.query(
                LearningDbHelper.TABLE_LESSONS,
                null,
                "${LearningDbHelper.COL_LESSON_COURSE_ID} = ?",
                arrayOf(courseId.toString()),
                null, null,
                "${LearningDbHelper.COL_LESSON_ID} ASC"
            )
            val lessons = mutableListOf<LessonEntity>()
            lessonsCursor.use {
                while (it.moveToNext()) {
                    lessons.add(it.readLessonEntity())
                }
            }

            CourseWithLessons(course, lessons)
        }
    }

    override suspend fun getLesson(lessonId: Long): LessonEntity? = withContext(Dispatchers.IO) {
        mutex.withLock {
            val db = dbHelper.readableDatabase
            val cursor = db.query(
                LearningDbHelper.TABLE_LESSONS,
                null,
                "${LearningDbHelper.COL_LESSON_ID} = ?",
                arrayOf(lessonId.toString()),
                null, null, null
            )
            cursor.use {
                if (it.moveToNext()) it.readLessonEntity() else null
            }
        }
    }

    override suspend fun getLessonsForCourse(courseId: Long): List<LessonEntity> = withContext(Dispatchers.IO) {
        mutex.withLock {
            val db = dbHelper.readableDatabase
            val cursor = db.query(
                LearningDbHelper.TABLE_LESSONS,
                null,
                "${LearningDbHelper.COL_LESSON_COURSE_ID} = ?",
                arrayOf(courseId.toString()),
                null, null,
                "${LearningDbHelper.COL_LESSON_ID} ASC"
            )
            val list = mutableListOf<LessonEntity>()
            cursor.use {
                while (it.moveToNext()) {
                    list.add(it.readLessonEntity())
                }
            }
            list
        }
    }

    override suspend fun insertCourses(courses: List<CourseEntity>) = withContext(Dispatchers.IO) {
        mutex.withLock {
            val db = dbHelper.writableDatabase
            db.beginTransaction()
            try {
                courses.forEach { course ->
                    val values = ContentValues().apply {
                        put(LearningDbHelper.COL_COURSE_ID, course.id)
                        put(LearningDbHelper.COL_COURSE_TITLE, course.title)
                        put(LearningDbHelper.COL_COURSE_INSTRUCTOR, course.instructor)
                        put(LearningDbHelper.COL_COURSE_PROGRESS, course.progress)
                        put(LearningDbHelper.COL_COURSE_LESSONS_COUNT, course.lessonsCount)
                        put(LearningDbHelper.COL_COURSE_CATEGORY, course.category)
                        put(LearningDbHelper.COL_COURSE_IS_CERTIFIED, if (course.isCertified) 1 else 0)
                        put(LearningDbHelper.COL_COURSE_DURATION, course.totalDurationHours)
                        put(LearningDbHelper.COL_COURSE_QUIZ_SCORE, course.quizScorePercent)
                        put(LearningDbHelper.COL_COURSE_LAST_UPDATED, course.lastUpdated)
                    }
                    db.insertWithOnConflict(
                        LearningDbHelper.TABLE_COURSES,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE
                    )
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
        notifyChanged()
    }

    override suspend fun insertLessons(lessons: List<LessonEntity>) = withContext(Dispatchers.IO) {
        mutex.withLock {
            val db = dbHelper.writableDatabase
            db.beginTransaction()
            try {
                lessons.forEach { lesson ->
                    val values = ContentValues().apply {
                        put(LearningDbHelper.COL_LESSON_ID, lesson.id)
                        put(LearningDbHelper.COL_LESSON_COURSE_ID, lesson.courseId)
                        put(LearningDbHelper.COL_LESSON_TITLE, lesson.title)
                        put(LearningDbHelper.COL_LESSON_DURATION, lesson.durationMinutes)
                        put(LearningDbHelper.COL_LESSON_TYPE, lesson.type)
                        put(LearningDbHelper.COL_LESSON_DESCRIPTION, lesson.description)
                        put(LearningDbHelper.COL_LESSON_IS_COMPLETED, if (lesson.isCompleted) 1 else 0)
                    }
                    db.insertWithOnConflict(
                        LearningDbHelper.TABLE_LESSONS,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE
                    )
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
        notifyChanged()
    }

    override suspend fun updateLessonCompleted(lessonId: Long, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        mutex.withLock {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(LearningDbHelper.COL_LESSON_IS_COMPLETED, if (isCompleted) 1 else 0)
            }
            db.update(
                LearningDbHelper.TABLE_LESSONS,
                values,
                "${LearningDbHelper.COL_LESSON_ID} = ?",
                arrayOf(lessonId.toString())
            )
        }
        notifyChanged()
    }

    override suspend fun updateCourseProgress(courseId: Long, progress: Int) = withContext(Dispatchers.IO) {
        mutex.withLock {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put(LearningDbHelper.COL_COURSE_PROGRESS, progress)
            }
            db.update(
                LearningDbHelper.TABLE_COURSES,
                values,
                "${LearningDbHelper.COL_COURSE_ID} = ?",
                arrayOf(courseId.toString())
            )
        }
        notifyChanged()
    }

    override suspend fun upsertPreservingLocalCompletion(
        remoteCourses: List<CourseEntity>,
        remoteLessons: List<LessonEntity>
    ) = withContext(Dispatchers.IO) {
        insertCourses(remoteCourses)

        // Read local completion status for lessons
        val mergedLessons = remoteLessons.map { remoteLesson ->
            val existing = getLesson(remoteLesson.id)
            if (existing != null && existing.isCompleted) {
                remoteLesson.copy(isCompleted = true)
            } else {
                remoteLesson
            }
        }
        insertLessons(mergedLessons)

        // Recalculate progress for each course
        remoteCourses.forEach { course ->
            val allLessons = getLessonsForCourse(course.id)
            if (allLessons.isNotEmpty()) {
                val completed = allLessons.count { it.isCompleted }
                val calculated = ((completed.toDouble() / allLessons.size.toDouble()) * 100).toInt().coerceIn(0, 100)
                updateCourseProgress(course.id, calculated)
            }
        }
        notifyChanged()
    }

    private fun Cursor.readCourseEntity(): CourseEntity {
        return CourseEntity(
            id = getLong(getColumnIndexOrThrow(LearningDbHelper.COL_COURSE_ID)),
            title = getString(getColumnIndexOrThrow(LearningDbHelper.COL_COURSE_TITLE)),
            instructor = getString(getColumnIndexOrThrow(LearningDbHelper.COL_COURSE_INSTRUCTOR)),
            progress = getInt(getColumnIndexOrThrow(LearningDbHelper.COL_COURSE_PROGRESS)),
            lessonsCount = getInt(getColumnIndexOrThrow(LearningDbHelper.COL_COURSE_LESSONS_COUNT)),
            category = getString(getColumnIndexOrThrow(LearningDbHelper.COL_COURSE_CATEGORY)),
            isCertified = getInt(getColumnIndexOrThrow(LearningDbHelper.COL_COURSE_IS_CERTIFIED)) == 1,
            totalDurationHours = getDouble(getColumnIndexOrThrow(LearningDbHelper.COL_COURSE_DURATION)),
            quizScorePercent = getInt(getColumnIndexOrThrow(LearningDbHelper.COL_COURSE_QUIZ_SCORE)),
            lastUpdated = getLong(getColumnIndexOrThrow(LearningDbHelper.COL_COURSE_LAST_UPDATED))
        )
    }

    private fun Cursor.readLessonEntity(): LessonEntity {
        return LessonEntity(
            id = getLong(getColumnIndexOrThrow(LearningDbHelper.COL_LESSON_ID)),
            courseId = getLong(getColumnIndexOrThrow(LearningDbHelper.COL_LESSON_COURSE_ID)),
            title = getString(getColumnIndexOrThrow(LearningDbHelper.COL_LESSON_TITLE)),
            durationMinutes = getInt(getColumnIndexOrThrow(LearningDbHelper.COL_LESSON_DURATION)),
            type = getString(getColumnIndexOrThrow(LearningDbHelper.COL_LESSON_TYPE)),
            description = getString(getColumnIndexOrThrow(LearningDbHelper.COL_LESSON_DESCRIPTION)),
            isCompleted = getInt(getColumnIndexOrThrow(LearningDbHelper.COL_LESSON_IS_COMPLETED)) == 1
        )
    }
}
