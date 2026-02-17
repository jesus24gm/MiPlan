package com.miplan.domain.model

/**
 * Modelo de dominio para Notificación
 */
data class Notification(
    val id: Int,
    val userId: Int,
    val taskId: Int?,
    val message: String,
    val type: NotificationType,
    val isRead: Boolean,
    val createdAt: String
)

/**
 * Tipo de notificación
 */
enum class NotificationType(val displayName: String) {
    TASK_REMINDER("Recordatorio de Tarea"),
    TASK_ASSIGNED("Tarea Asignada"),
    TASK_COMPLETED("Tarea Completada"),
    SYSTEM("Sistema");

    companion object {
        fun fromString(value: String): NotificationType {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: SYSTEM
        }
    }
}
