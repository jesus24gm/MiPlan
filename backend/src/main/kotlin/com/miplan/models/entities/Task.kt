package com.miplan.models.entities

import java.time.LocalDateTime

/**
 * Entidad de Tarea
 */
data class Task(
    val id: Int,
    val title: String,
    val description: String?,
    val status: String,
    val priority: String,
    val dueDate: LocalDateTime?,
    val imageUrl: String?,
    val boardId: Int?,
    val createdBy: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    // Campos de recurrencia
    val recurrenceType: String = "NONE",
    val recurrenceInterval: Int = 1,
    val recurrenceDays: String? = null, // Almacenado como "1,3,5" para lunes, miércoles, viernes
    val recurrenceEndDate: LocalDateTime? = null,
    val isRecurringInstance: Boolean = false,
    val parentTaskId: Int? = null
)

/**
 * Estados de tarea
 */
enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

/**
 * Prioridades de tarea
 */
enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH
}

/**
 * Tipos de recurrencia
 */
enum class RecurrenceType {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

/**
 * Días de la semana para recurrencia
 */
enum class DayOfWeek(val value: Int) {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7)
}
