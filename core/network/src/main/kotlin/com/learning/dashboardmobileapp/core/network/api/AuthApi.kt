package com.learning.dashboardmobileapp.core.network.api

import com.learning.dashboardmobileapp.core.domain.util.AuthConstants
import com.learning.dashboardmobileapp.core.domain.util.CourseConstants
import com.learning.dashboardmobileapp.core.network.model.LoginRequestDto
import com.learning.dashboardmobileapp.core.network.model.LoginResponseDto
import com.learning.dashboardmobileapp.core.network.monitor.NetworkMonitor
import kotlinx.coroutines.delay
import java.io.IOException

interface AuthApi {
    suspend fun login(request: LoginRequestDto): LoginResponseDto
}

class MockAuthApi(
    private val networkMonitor: NetworkMonitor,
    private val courseApi: CourseApi
) : AuthApi {

    override suspend fun login(request: LoginRequestDto): LoginResponseDto {
        // Simulate network latency (800ms)
        delay(800)

        // Connectivity check
        if (courseApi.simulateOfflineFlow.value || !networkMonitor.isCurrentlyOnline()) {
            throw IOException(CourseConstants.ERROR_NETWORK_CONNECTION)
        }

        // Validate mock credentials:
        // Any valid email with "password123" succeeds
        if (request.password == AuthConstants.DEMO_PASSWORD) {
            return LoginResponseDto(
                token = "mock-jwt-token-xyz-123456",
                userId = "usr_1001",
                email = request.email,
                name = request.email.substringBefore("@").replaceFirstChar { it.uppercase() }
            )
        } else {
            throw IllegalArgumentException(AuthConstants.ERROR_INVALID_CREDENTIALS)
        }
    }
}
