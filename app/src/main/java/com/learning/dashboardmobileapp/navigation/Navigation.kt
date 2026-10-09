package com.learning.dashboardmobileapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.learning.dashboardmobileapp.feature.auth.LoginRoute
import com.learning.dashboardmobileapp.feature.coursedetail.CourseDetailRoute
import com.learning.dashboardmobileapp.feature.dashboard.DashboardRoute
import kotlinx.serialization.Serializable

@Serializable
data object LoginDestination

@Serializable
data object DashboardDestination

@Serializable
data class CourseDetailDestination(val courseId: Long)

@Composable
fun LearningNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = LoginDestination
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
