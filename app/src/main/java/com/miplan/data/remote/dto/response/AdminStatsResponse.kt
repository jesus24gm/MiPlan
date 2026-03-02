package com.miplan.data.remote.dto.response

import com.miplan.domain.model.AdminStats
import kotlinx.serialization.Serializable

@Serializable
data class AdminStatsResponse(
    val totalUsers: Int,
    val totalTasks: Int,
    val totalBoards: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val activeUsers: Int
) {
    fun toDomain(): AdminStats {
        return AdminStats(
            totalUsers = totalUsers,
            totalTasks = totalTasks,
            totalBoards = totalBoards,
            completedTasks = completedTasks,
            pendingTasks = pendingTasks,
            activeUsers = activeUsers
        )
    }
}
