package com.learning.dashboardmobileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.learning.dashboardmobileapp.core.data.security.SecurityUtils
import com.learning.dashboardmobileapp.core.ui.theme.LearningDashboardTheme
import com.learning.dashboardmobileapp.navigation.LearningNavHost

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Security: Enable FLAG_SECURE to prevent screen capture, screen recording,
        // and sensitive recent tasks thumbnail leakage
        SecurityUtils.enableSecureWindow(window)

        enableEdgeToEdge()
        setContent {
            LearningDashboardTheme {
                LearningNavHost()
            }
        }
    }
}