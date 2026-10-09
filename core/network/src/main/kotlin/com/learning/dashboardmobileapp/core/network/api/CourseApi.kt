package com.learning.dashboardmobileapp.core.network.api

import com.learning.dashboardmobileapp.core.domain.util.CourseConstants
import com.learning.dashboardmobileapp.core.network.model.CourseDto
import com.learning.dashboardmobileapp.core.network.monitor.NetworkMonitor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException

interface CourseApi {
    val simulateOfflineFlow: StateFlow<Boolean>
    fun setSimulateOffline(simulate: Boolean)
    suspend fun fetchCourses(): List<CourseDto>
}

class MockCourseApi(
    private val networkMonitor: NetworkMonitor
) : CourseApi {

    private val _simulateOffline = MutableStateFlow(false)
    override val simulateOfflineFlow: StateFlow<Boolean> = _simulateOffline.asStateFlow()

    override fun setSimulateOffline(simulate: Boolean) {
        _simulateOffline.value = simulate
    }

    override suspend fun fetchCourses(): List<CourseDto> {
        // Simulate network latency (600ms)
        delay(600)

        // Connectivity check: either simulated offline OR actual device is offline
        if (_simulateOffline.value || !networkMonitor.isCurrentlyOnline()) {
            throw IOException(CourseConstants.ERROR_NETWORK_COULD_NOT_REACH)
        }

        return MockData.getInitialCourses()
    }
}
