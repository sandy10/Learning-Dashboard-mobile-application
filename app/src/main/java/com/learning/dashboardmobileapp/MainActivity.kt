package com.learning.dashboardmobileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.learning.dashboardmobileapp.core.ui.theme.LearningDashboardTheme
import com.learning.dashboardmobileapp.navigation.LearningNavHost

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LearningDashboardTheme {
                LearningNavHost()
            }
        }
    }
}