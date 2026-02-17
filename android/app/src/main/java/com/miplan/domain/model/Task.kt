package com.miplan.domain.model

/**
 * Modelo de dominio para Tarea
 */
data class Task(
    val id: Int,
    val title: String,
    val description: String?,
    val status: TaskStatus,
    val priority: TaskPriority,
    val dueDate: String?,
    val boardId: Int?,
    val boardName: String?,
    val createdBy: Int,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Estado de una tarea
 */
enum class TaskStatus(val displayName: String) {
    PENDING("Pendiente"),
    IN_PROGRESS("En Progreso"),
    COMPLETED("Completada"),
    CANCELLED("Cancelada");

    companion object {
        fun fromString(value: String): TaskStatus {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: PENDING
        }
    }
}

/**
 * Prioridad de una tarea
 */
enum class TaskPriority(val displayName: String) {
    LOW("Baja"),
    MEDIUM("Media"),
    HIGH("Alta");

    companion object {
        fun fromString(value: String): TaskPriority {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: MEDIUM
        }
    }
}
