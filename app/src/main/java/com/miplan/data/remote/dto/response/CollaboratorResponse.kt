package com.miplan.data.remote.dto.response

import com.miplan.domain.model.CollaboratorRole
import com.miplan.domain.model.TaskCollaborator
import kotlinx.serialization.Serializable

/**
 * DTO de respuesta para colaborador
 */
@Serializable
data class CollaboratorDto(
    val taskId: Int,
    val userId: Int,
    val userName: String,
    val userEmail: String,
    val userAvatarUrl: String? = null,
    val role: String,
    val addedAt: String,
    val addedBy: Int
) {
    fun toDomain(): TaskCollaborator {
        return TaskCollaborator(
            taskId = taskId,
            userId = userId,
            userName = userName,
            userEmail = userEmail,
            userAvatarUrl = userAvatarUrl,
            role = CollaboratorRole.fromString(role),
            addedAt = addedAt,
            addedBy = addedBy
        )
    }
}

/**
 * Request para agregar colaborador
 */
@Serializable
data class AddCollaboratorRequest(
    val userEmail: String,
    val role: String = "VIEWER"
)

/**
 * Request para actualizar rol de colaborador
 */
@Serializable
data class UpdateCollaboratorRoleRequest(
    val role: String
)
