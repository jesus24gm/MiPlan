package com.miplan.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.miplan.domain.model.Task
import com.miplan.domain.model.TaskPriority
import com.miplan.domain.model.TaskStatus
import com.miplan.domain.model.RecurrenceType
import com.miplan.domain.model.DayOfWeek

/**
 * Entidad de Room para tareas
 * Representa una tarea almacenada localmente
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,
    val serverId: Int? = null, // ID del servidor, null si no está sincronizado
    val title: String,
    val description: String?,
    val status: String,
    val priority: String,
    val dueDate: String?,
    val imageUrl: String?,
    val boardId: Int?,
    val createdBy: Int?,
    val createdAt: String,
    val updatedAt: String,
    val isSynced: Boolean = false, // Flag para indicar si está sincronizado con el servidor
    val isDeleted: Boolean = false, // Soft delete para sincronización
    // Campos de recurrencia
    val recurrenceType: String = "NONE",
    val recurrenceInterval: Int = 1,
    val recurrenceDays: String? = null, // Almacenado como "1,3,5" para lunes, miércoles, viernes
    val recurrenceEndDate: String? = null,
    val isRecurringInstance: Boolean = false,
    val parentTaskId: Int? = null
) {
    /**
     * Convierte la entidad de Room a modelo de dominio
     * IMPORTANTE: Siempre usa localId como identificador para mantener consistencia en la UI
     */
    fun toDomain(): Task {
        return Task(
            id = localId, // Usar localId para consistencia (no cambia después de sincronizar)
            title = title,
            description = description,
            status = TaskStatus.fromString(status),
            priority = TaskPriority.fromString(priority),
            dueDate = dueDate,
            imageUrl = imageUrl,
            boardId = boardId,
            boardName = null,
            createdBy = createdBy ?: 0,
            createdAt = createdAt,
            updatedAt = updatedAt,
            recurrenceType = RecurrenceType.fromString(recurrenceType),
            recurrenceInterval = recurrenceInterval,
            recurrenceDays = recurrenceDays?.split(",")?.mapNotNull { it.toIntOrNull() }?.map { DayOfWeek.fromValue(it) },
            recurrenceEndDate = recurrenceEndDate,
            isRecurringInstance = isRecurringInstance,
            parentTaskId = parentTaskId
        )
    }
    
    companion object {
        /**
         * Crea una entidad de Room desde un modelo de dominio
         */
        fun fromDomain(task: Task, isSynced: Boolean = true): TaskEntity {
            return TaskEntity(
                serverId = task.id,
                title = task.title,
                description = task.description,
                status = task.status.name,
                priority = task.priority.name,
                dueDate = task.dueDate,
                imageUrl = task.imageUrl,
                boardId = task.boardId,
                createdBy = task.createdBy,
                createdAt = task.createdAt,
                updatedAt = task.updatedAt,
                isSynced = isSynced,
                recurrenceType = task.recurrenceType.name,
                recurrenceInterval = task.recurrenceInterval,
                recurrenceDays = task.recurrenceDays?.joinToString(",") { it.value.toString() },
                recurrenceEndDate = task.recurrenceEndDate,
                isRecurringInstance = task.isRecurringInstance,
                parentTaskId = task.parentTaskId
            )
        }
    }
}
