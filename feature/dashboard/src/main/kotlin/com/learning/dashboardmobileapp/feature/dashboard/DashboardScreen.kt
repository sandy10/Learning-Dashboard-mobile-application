package com.learning.dashboardmobileapp.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learning.dashboardmobileapp.core.domain.model.Course
import com.learning.dashboardmobileapp.core.ui.UiConstants
import com.learning.dashboardmobileapp.core.ui.components.EmptyCoursesCard
import com.learning.dashboardmobileapp.core.ui.components.ErrorCard
import com.learning.dashboardmobileapp.core.ui.components.LearningProgressBar
import com.learning.dashboardmobileapp.core.ui.components.OfflineStatusBanner
import com.learning.dashboardmobileapp.core.ui.components.ShimmerCourseCard
import com.learning.dashboardmobileapp.core.ui.theme.AppBackground
import com.learning.dashboardmobileapp.core.ui.theme.NeutralPendingBg
import com.learning.dashboardmobileapp.core.ui.theme.NeutralPendingText
import com.learning.dashboardmobileapp.core.ui.theme.PrimaryIndigo
import com.learning.dashboardmobileapp.core.ui.theme.PrimaryLight
import com.learning.dashboardmobileapp.core.ui.theme.SurfaceSubtle
import com.learning.dashboardmobileapp.core.ui.theme.TertiarySuccessLight
import com.learning.dashboardmobileapp.core.ui.theme.TertiarySuccessText
import com.learning.dashboardmobileapp.core.ui.theme.TextMuted
import com.learning.dashboardmobileapp.core.ui.theme.WarningYellowBg
import com.learning.dashboardmobileapp.core.ui.theme.WarningYellowBorder
import com.learning.dashboardmobileapp.core.ui.theme.WarningYellowText
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardRoute(
    onCourseClicked: (Long) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is DashboardUiEvent.NavigateToCourseDetails -> onCourseClicked(event.courseId)
                DashboardUiEvent.NavigateToLogin -> onNavigateToLogin()
                is DashboardUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    DashboardScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: DashboardUiState,
    onAction: (DashboardUiAction) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AppBackground
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onAction(DashboardUiAction.OnRefresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header: Greeting + Graduation cap badge + Logout
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = UiConstants.GREETING_TEXT,
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = UiConstants.GREETING_EMOJI, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = UiConstants.DASHBOARD_SUBTITLE,
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = UiConstants.STUDENT_PROFILE_DESC,
                                    tint = PrimaryIndigo,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            IconButton(onClick = { onAction(DashboardUiAction.OnLogout) }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = UiConstants.LOGOUT_DESC,
                                    tint = TextMuted
                                )
                            }
                        }
                    }
                }

                // State Preview Selector (matching Stitch design bar)
                item {
                    StatePreviewSelector(
                        currentMode = state.previewMode,
                        onModeSelected = { onAction(DashboardUiAction.OnPreviewModeChanged(it)) }
                    )
                }

                // Offline status banner + Simulate Offline button
                item {
                    OfflineStatusBanner(
                        isOnline = state.isOnline,
                        isSimulateOffline = state.isSimulateOffline,
                        onToggleSimulateOffline = { onAction(DashboardUiAction.OnToggleSimulateOffline) }
                    )
                }

                // Summary Active Curriculum Card
                item {
                    ActiveCurriculumSummaryCard(totalEnrolled = state.totalEnrolled)
                }

                // Section Title: My Courses
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = UiConstants.MY_COURSES_TITLE,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = UiConstants.SORTED_BY_RECENT,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PrimaryIndigo,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // Show State Preview Overrides or real state
                when (state.previewMode) {
                    DashboardPreviewMode.SKELETON -> {
                        items(3) { ShimmerCourseCard() }
                    }

                    DashboardPreviewMode.EMPTY -> {
                        item {
                            EmptyCoursesCard(onRefresh = { onAction(DashboardUiAction.OnRefresh) })
                        }
                    }

                    DashboardPreviewMode.ERROR -> {
                        item {
                            ErrorCard(
                                message = UiConstants.NETWORK_CONNECTION_FAILED,
                                onRetry = { onAction(DashboardUiAction.OnRefresh) }
                            )
                        }
                    }

                    DashboardPreviewMode.NORMAL -> {
                        if (state.isLoading && state.courses.isEmpty()) {
                            items(3) { ShimmerCourseCard() }
                        } else if (state.courses.isEmpty()) {
                            if (state.errorMessage != null) {
                                item {
                                    ErrorCard(
                                        message = state.errorMessage,
                                        onRetry = { onAction(DashboardUiAction.OnRefresh) }
                                    )
                                }
                            } else {
                                item {
                                    EmptyCoursesCard(onRefresh = { onAction(DashboardUiAction.OnRefresh) })
                                }
                            }
                        } else {
                            // If there's an offline warning while displaying cached courses, show a banner
                            if (state.errorMessage != null) {
                                item {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = WarningYellowBg,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, WarningYellowBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = state.errorMessage,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = WarningYellowText,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                            }

                            items(
                                items = state.courses,
                                key = { it.id }
                            ) { course ->
                                CourseCard(
                                    course = course,
                                    onClick = { onAction(DashboardUiAction.OnCourseClicked(course.id)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatePreviewSelector(
    currentMode: DashboardPreviewMode,
    onModeSelected: (DashboardPreviewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = NeutralPendingBg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DashboardPreviewMode.entries.forEach { mode ->
                val isSelected = currentMode == mode
                val label = when (mode) {
                    DashboardPreviewMode.NORMAL -> UiConstants.PREVIEW_MODE_NORMAL
                    DashboardPreviewMode.SKELETON -> UiConstants.PREVIEW_MODE_SKELETON
                    DashboardPreviewMode.EMPTY -> UiConstants.PREVIEW_MODE_EMPTY
                    DashboardPreviewMode.ERROR -> UiConstants.PREVIEW_MODE_ERROR
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) PrimaryIndigo else Color.Transparent)
                        .clickable { onModeSelected(mode) }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isSelected) Color.White else NeutralPendingText,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveCurriculumSummaryCard(
    totalEnrolled: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = UiConstants.ACTIVE_CURRICULUM_TITLE,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = UiConstants.YOUR_LEARNING_TITLE,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$totalEnrolled ${UiConstants.COURSES_ENROLLED_SUFFIX}",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                )
            }

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PrimaryLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = PrimaryIndigo,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun CourseCard(
    course: Course,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Top Row: Course Icon + Title & Instructor + "Offline ready" badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Course icon box
                val icon: ImageVector = when (course.id) {
                    1L -> Icons.Default.Terminal
                    2L -> Icons.Default.AutoAwesome
                    else -> Icons.Default.Layers
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NeutralPendingBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PrimaryIndigo,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${UiConstants.INSTRUCTOR_PREFIX}${course.instructor}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }

                // Offline ready badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceSubtle
                ) {
                    Text(
                        text = UiConstants.OFFLINE_READY_BADGE,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            LearningProgressBar(progress = course.progress)

            Spacer(modifier = Modifier.height(8.dp))

            // Lessons completed info & cached indicator
            val completedCount = ((course.progress.toDouble() / 100.0) * course.lessonsCount).toInt()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$completedCount of ${course.lessonsCount} ${UiConstants.LESSONS_COMPLETED_SUFFIX}",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = TertiarySuccessText,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = UiConstants.CACHED_INDICATOR,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TertiarySuccessText,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom row: Lessons count + Continue Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${course.lessonsCount} ${UiConstants.LESSONS_SUFFIX}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NeutralPendingText,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = UiConstants.CONTINUE_BUTTON_TEXT,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
