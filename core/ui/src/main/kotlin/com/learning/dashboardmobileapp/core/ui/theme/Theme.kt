package com.learning.dashboardmobileapp.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

val LearningShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun LearningDashboardTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LearningColorScheme,
        typography = LearningTypography,
        shapes = LearningShapes,
        content = content
    )
}
