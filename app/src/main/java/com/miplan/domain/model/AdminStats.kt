package com.miplan.domain.model

/**
 * Modelo de dominio para estadísticas de administración
 */
data class AdminStats(
    val totalUsers: Int,
    val totalTasks: Int,
    val totalBoards: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val activeUsers: Int
)
