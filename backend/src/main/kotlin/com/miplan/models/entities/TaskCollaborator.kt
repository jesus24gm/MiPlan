package com.miplan.models.entities

import java.time.LocalDateTime

/**
 * Entidad de colaborador de tarea
 */
data class TaskCollaborator(
    val taskId: Int,
    val userId: Int,
    val userName: String,
    val userEmail: String,
    val userAvatarUrl: String?,
    val role: CollaboratorRole,
    val addedAt: LocalDateTime,
    val addedBy: Int
)

/**
 * Roles de colaborador
 */
enum class CollaboratorRole {
    OWNER,   // Creador de la tarea, puede hacer todo
    EDITOR,  // Puede editar la tarea
    VIEWER   // Solo puede ver la tarea
}
