package com.learning.dashboardmobileapp.feature.coursedetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learning.dashboardmobileapp.core.domain.model.Course
import com.learning.dashboardmobileapp.core.domain.model.CourseDetails
import com.learning.dashboardmobileapp.core.domain.model.Lesson
import com.learning.dashboardmobileapp.core.ui.UiConstants
import com.learning.dashboardmobileapp.core.ui.components.LearningProgressBar
import com.learning.dashboardmobileapp.core.ui.components.LessonStatusChip
import com.learning.dashboardmobileapp.core.ui.theme.AppBackground
import com.learning.dashboardmobileapp.core.ui.theme.PrimaryIndigo
import com.learning.dashboardmobileapp.core.ui.theme.PrimaryLight
import com.learning.dashboardmobileapp.core.ui.theme.TertiarySuccess
import com.learning.dashboardmobileapp.core.ui.theme.TertiarySuccessLight
import com.learning.dashboardmobileapp.core.ui.theme.TertiarySuccessText
import org.koin.androidx.compose.koinViewModel

@Composable
fun CourseDetailRoute(
    onNavigateBack: () -> Unit,
    viewModel: CourseDetailViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                CourseDetailUiEvent.NavigateBack -> onNavigateBack()
                is CourseDetailUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    CourseDetailScreen(
        state = state,
        onAction = viewModel::onAction,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    state: CourseDetailUiState,
    onAction: (CourseDetailUiAction) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = UiConstants.APP_NAME,
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF6C757D))
                        )
                        Text(
                            text = UiConstants.COURSE_OVERVIEW_TITLE,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(CourseDetailUiAction.OnNavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = UiConstants.BACK_BUTTON_DESC,
                            tint = Color(0xFF191C1E)
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = PrimaryIndigo,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppBackground)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AppBackground
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryIndigo)
            }
        } else if (state.courseDetails == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = UiConstants.COURSE_NOT_FOUND,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            val details = state.courseDetails
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Synced indicator row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = TertiarySuccessLight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, TertiarySuccessText.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(TertiarySuccessText)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = UiConstants.SYNCED_BADGE,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TertiarySuccessText,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = UiConstants.ONLINE_BADGE,
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF6C757D))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = PrimaryIndigo,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Course Header Main Card
                item {
                    CourseMainHeaderCard(course = details.course)
                }

                // Quick Stats 3-column row
                item {
                    QuickStatsRow(course = details.course)
                }

                // Lessons Section Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = UiConstants.LESSONS_SECTION_TITLE,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFE9ECEF)
                            ) {
                                Text(
                                    text = "${details.lessons.size} ${UiConstants.LISTED_SUFFIX}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF495057),
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = UiConstants.MODULE_INFO,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PrimaryIndigo,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                // Interactive Lesson Items
                items(
                    items = details.lessons,
                    key = { it.id }
                ) { lesson ->
                    val isExpanded = state.expandedLessonId == lesson.id
                    LessonItemCard(
                        lesson = lesson,
                        isExpanded = isExpanded,
                        isMarkingComplete = state.isMarkingComplete,
                        onToggleExpand = { onAction(CourseDetailUiAction.OnToggleLessonExpanded(lesson.id)) },
                        onMarkCompleted = { onAction(CourseDetailUiAction.OnMarkLessonCompleted(lesson.id)) }
                    )
                }

                // Footer: Downloaded Course Content (from Stitch design)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(PrimaryLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderZip,
                                    contentDescription = null,
                                    tint = PrimaryIndigo,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = UiConstants.DOWNLOADED_CONTENT_TITLE,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "All ${details.course.lessonsCount} lessons available offline with exercise files.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6C757D))
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = Color(0xFFADB5BD),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CourseMainHeaderCard(
    course: Course,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Category tag + Certified badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFEDE9FE)
                ) {
                    Text(
                        text = course.category,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PrimaryIndigo,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = TertiarySuccessText,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = UiConstants.CERTIFIED_BADGE,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TertiarySuccessText,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Course Title
            Text(
                text = course.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${UiConstants.INSTRUCTOR_DETAIL_PREFIX}${course.instructor}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6C757D))
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Course Progress Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF8F9FA)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    LearningProgressBar(progress = course.progress)

                    Spacer(modifier = Modifier.height(8.dp))

                    val completedCount = ((course.progress.toDouble() / 100.0) * course.lessonsCount).toInt()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$completedCount of ${course.lessonsCount} ${UiConstants.LESSONS_COMPLETED_SUFFIX}",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF6C757D))
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = TertiarySuccessText,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = UiConstants.ON_TRACK_BADGE,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TertiarySuccessText,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickStatsRow(
    course: Course,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuickStatCard(
            label = UiConstants.TOTAL_TIME_LABEL,
            value = "${course.totalDurationHours} ${UiConstants.HOURS_SUFFIX}",
            valueColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        QuickStatCard(
            label = UiConstants.DOWNLOADED_LABEL,
            value = "${course.lessonsCount} / ${course.lessonsCount}",
            valueColor = Color(0xFF00687A),
            modifier = Modifier.weight(1f)
        )
        QuickStatCard(
            label = UiConstants.QUIZ_SCORE_LABEL,
            value = "${course.quizScorePercent}%",
            valueColor = TertiarySuccessText,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickStatCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF6C757D),
                    fontSize = 11.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = valueColor
                )
            )
        }
    }
}

@Composable
fun LessonItemCard(
    lesson: Lesson,
    isExpanded: Boolean,
    isMarkingComplete: Boolean,
    onToggleExpand: () -> Unit,
    onMarkCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleExpand)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circle status icon
                if (lesson.isCompleted) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(TertiarySuccessLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = UiConstants.STATUS_COMPLETED,
                            tint = TertiarySuccessText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F3F5))
                            .border(1.5.dp, Color(0xFFCED4DA), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFADB5BD))
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lesson.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${lesson.durationMinutes} ${UiConstants.MINS_SEPARATOR}${lesson.type.displayName}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF6C757D))
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                LessonStatusChip(isCompleted = lesson.isCompleted)

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF6C757D),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Expanded section
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                ) {
                    if (lesson.description.isNotEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF8F9FA)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = PrimaryIndigo,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = lesson.description,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF495057))
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (!lesson.isCompleted) {
                        Button(
                            onClick = onMarkCompleted,
                            enabled = !isMarkingComplete,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            if (isMarkingComplete) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = UiConstants.MARK_AS_COMPLETED_BUTTON,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = TertiarySuccessLight
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = TertiarySuccessText,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = UiConstants.LESSON_COMPLETED_BANNER,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = TertiarySuccessText,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
