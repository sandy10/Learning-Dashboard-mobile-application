package com.learning.dashboardmobileapp.core.domain.util

import kotlin.math.roundToInt

object ProgressCalculator {

    /**
     * Calculates the completion percentage from completed lessons and total lessons.
     * Guaranteed to be clamped between 0 and 100.
     * When totalLessons <= 0, returns 0.
     */
    fun calculateProgress(completedLessons: Int, totalLessons: Int): Int {
        if (totalLessons <= 0) return 0
        if (completedLessons <= 0) return 0
        val percentage = (completedLessons.toDouble() / totalLessons.toDouble()) * 100.0
        return percentage.roundToInt().coerceIn(0, 100)
    }
}
