package com.miplan.models.responses

import kotlinx.serialization.Serializable

/**
 * Response con las estadísticas del usuario
 */
@Serializable
data class UserStatsResponse(
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val totalBoards: Int,
    val activeBoards: Int,
    val completionRate: Int
)

/**
 * Response con estadísticas del sistema (Admin)
 */
@Serializable
data class AdminStatsResponse(
    val totalUsers: Int,
    val activeUsers: Int,
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val totalBoards: Int
)
