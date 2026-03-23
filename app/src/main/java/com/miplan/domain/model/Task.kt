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
    val imageUrl: String?,
    val boardId: Int?,
    val boardName: String?,
    val createdBy: Int,
    val createdAt: String,
    val updatedAt: String,
    // Campos de recurrencia
    val recurrenceType: RecurrenceType = RecurrenceType.NONE,
    val recurrenceInterval: Int = 1, // Cada cuántos días/semanas/meses se repite
    val recurrenceDays: List<DayOfWeek>? = null, // Para recurrencia semanal: qué días
    val recurrenceEndDate: String? = null, // Fecha límite para dejar de repetir
    val isRecurringInstance: Boolean = false, // Si es una instancia generada automáticamente
    val parentTaskId: Int? = null // ID de la tarea padre si es instancia recurrente
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

/**
 * Tipo de recurrencia para tareas repetitivas
 */
enum class RecurrenceType(val displayName: String) {
    NONE("Sin repetir"),
    DAILY("Diaria"),
    WEEKLY("Semanal"),
    MONTHLY("Mensual"),
    YEARLY("Anual");

    companion object {
        fun fromString(value: String): RecurrenceType {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: NONE
        }
    }
}

/**
 * Día de la semana para recurrencia semanal
 */
enum class DayOfWeek(val displayName: String, val value: Int) {
    MONDAY("Lunes", 1),
    TUESDAY("Martes", 2),
    WEDNESDAY("Miércoles", 3),
    THURSDAY("Jueves", 4),
    FRIDAY("Viernes", 5),
    SATURDAY("Sábado", 6),
    SUNDAY("Domingo", 7);

    companion object {
        fun fromValue(value: Int): DayOfWeek {
            return values().find { it.value == value } ?: MONDAY
        }
    }
}
