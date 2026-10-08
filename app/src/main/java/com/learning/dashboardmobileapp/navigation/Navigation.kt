package com.learning.dashboardmobileapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.learning.dashboardmobileapp.core.domain.usecase.ObserveAuthStateUseCase
import com.learning.dashboardmobileapp.feature.auth.LoginRoute
import com.learning.dashboardmobileapp.feature.coursedetail.CourseDetailRoute
import com.learning.dashboardmobileapp.feature.dashboard.DashboardRoute
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
data object LoginDestination

@Serializable
data object DashboardDestination

@Serializable
data class CourseDetailDestination(val courseId: Long)

@Composable
fun LearningNavHost(
    navController: NavHostController = rememberNavController(),
    observeAuthStateUseCase: ObserveAuthStateUseCase = koinInject()
) {
    var startDestinationDetermined by remember { mutableStateOf(false) }
    var startDestination: Any by remember { mutableStateOf(LoginDestination) }

    LaunchedEffect(Unit) {
        val isLoggedIn = observeAuthStateUseCase().first()
        startDestination = if (isLoggedIn) DashboardDestination else LoginDestination
        startDestinationDetermined = true
    }

    if (!startDestinationDetermined) {
        // Brief splash / wait until session check completes
        return
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<LoginDestination> {
            LoginRoute(
                onNavigateToDashboard = {
                    navController.navigate(DashboardDestination) {
                        popUpTo(LoginDestination) { inclusive = true }
                    }
                }
            )
        }

        composable<DashboardDestination> {
            DashboardRoute(
                onCourseClicked = { courseId ->
                    navController.navigate(CourseDetailDestination(courseId))
                },
                onNavigateToLogin = {
                    navController.navigate(LoginDestination) {
                        popUpTo(DashboardDestination) { inclusive = true }
                    }
                }
            )
        }

        composable<CourseDetailDestination> { backStackEntry ->
            val destination = backStackEntry.toRoute<CourseDetailDestination>()
            CourseDetailRoute(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
