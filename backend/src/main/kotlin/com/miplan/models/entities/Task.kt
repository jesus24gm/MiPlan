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
    val updatedAt: LocalDateTime
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
