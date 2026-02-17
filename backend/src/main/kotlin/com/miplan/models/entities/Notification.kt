package com.miplan.models.entities

import java.time.LocalDateTime

/**
 * Entidad de Notificación
 */
data class Notification(
    val id: Int,
    val userId: Int,
    val taskId: Int?,
    val message: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: LocalDateTime
)

/**
 * Tipos de notificación
 */
enum class NotificationType {
    TASK_REMINDER,
    TASK_ASSIGNED,
    TASK_COMPLETED,
    SYSTEM
}
