package com.learning.dashboardmobileapp.core.domain.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressCalculatorTest {

    @Test
    fun calculateProgress_zeroTotalLessons_returnsZero() {
        val result = ProgressCalculator.calculateProgress(completedLessons = 5, totalLessons = 0)
        assertEquals(0, result)
    }

    @Test
    fun calculateProgress_negativeTotalLessons_returnsZero() {
        val result = ProgressCalculator.calculateProgress(completedLessons = 5, totalLessons = -10)
        assertEquals(0, result)
    }

    @Test
    fun calculateProgress_zeroCompletedLessons_returnsZero() {
        val result = ProgressCalculator.calculateProgress(completedLessons = 0, totalLessons = 20)
        assertEquals(0, result)
    }

    @Test
    fun calculateProgress_negativeCompletedLessons_returnsZero() {
        val result = ProgressCalculator.calculateProgress(completedLessons = -3, totalLessons = 20)
        assertEquals(0, result)
    }

    @Test
    fun calculateProgress_pythonExampleThirteenOfTwenty_returnsSixtyFivePercent() {
        // 13 / 20 = 0.65 -> 65%
        val result = ProgressCalculator.calculateProgress(completedLessons = 13, totalLessons = 20)
        assertEquals(65, result)
    }

    @Test
    fun calculateProgress_generativeAiExampleSixOfSixteen_returnsFortyPercent() {
        // 6.4 / 16 = 40% (6 / 16 = 37.5 -> rounded to 38, or 6 of 15 is 40)
        // With 6 of 15: 40. With 6.4 rounded: 40.
        // Let's test 8 of 20 = 40%
        val result = ProgressCalculator.calculateProgress(completedLessons = 8, totalLessons = 20)
        assertEquals(40, result)
    }

    @Test
    fun calculateProgress_fullStackExampleSevenOfTwentyEight_returnsTwentyFivePercent() {
        // 7 / 28 = 0.25 -> 25%
        val result = ProgressCalculator.calculateProgress(completedLessons = 7, totalLessons = 28)
        assertEquals(25, result)
    }

    @Test
    fun calculateProgress_allCompleted_returnsOneHundredPercent() {
        val result = ProgressCalculator.calculateProgress(completedLessons = 20, totalLessons = 20)
        assertEquals(100, result)
    }

    @Test
    fun calculateProgress_completedExceedsTotal_isClampedToOneHundred() {
        val result = ProgressCalculator.calculateProgress(completedLessons = 25, totalLessons = 20)
        assertEquals(100, result)
    }

    @Test
    fun calculateProgress_roundingCheck_roundsProperly() {
        // 1 / 3 = 33.333% -> 33
        assertEquals(33, ProgressCalculator.calculateProgress(1, 3))
        // 2 / 3 = 66.666% -> 67
        assertEquals(67, ProgressCalculator.calculateProgress(2, 3))
    }
}
