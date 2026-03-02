package com.miplan.domain.model

/**
 * Modelo de colaborador de tarea
 */
data class TaskCollaborator(
    val taskId: Int,
    val userId: Int,
    val userName: String,
    val userEmail: String,
    val userAvatarUrl: String?,
    val role: CollaboratorRole,
    val addedAt: String,
    val addedBy: Int
)

/**
 * Roles de colaborador
 */
enum class CollaboratorRole(val displayName: String) {
    OWNER("Propietario"),
    EDITOR("Editor"),
    VIEWER("Visualizador");
    
    companion object {
        fun fromString(value: String): CollaboratorRole {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: VIEWER
        }
    }
}
