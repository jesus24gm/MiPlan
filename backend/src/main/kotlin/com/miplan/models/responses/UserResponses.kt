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
 * Response con información básica del usuario
 */
@Serializable
data class UserResponse(
    val id: Int,
    val email: String,
    val name: String,
    val role: String,
    val isVerified: Boolean,
    val createdAt: String
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
