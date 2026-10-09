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

        // Security: FLAG_SECURE can be enabled for production releases.
        // It is set to false here so that screen recording, video demonstration,
        // and Android Studio Device Mirroring capture all screens properly without a black screen.
        SecurityUtils.enableSecureWindow(window, enabled = false)

        enableEdgeToEdge()
        setContent {
            LearningDashboardTheme {
                LearningNavHost()
            }
        }
    }
}