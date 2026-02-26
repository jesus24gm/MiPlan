package com.miplan.models.responses

import kotlinx.serialization.Serializable

/**
 * Respuesta con información de colaborador
 */
@Serializable
data class CollaboratorResponse(
    val taskId: Int,
    val userId: Int,
    val userName: String,
    val userEmail: String,
    val userAvatarUrl: String?,
    val role: String,
    val addedAt: String,
    val addedBy: Int
)

/**
 * Respuesta con lista de colaboradores
 */
@Serializable
data class CollaboratorsListResponse(
    val taskId: Int,
    val collaborators: List<CollaboratorResponse>
)

/**
 * Request para agregar colaborador
 */
@Serializable
data class AddCollaboratorRequest(
    val userEmail: String,
    val role: String = "VIEWER"
)

/**
 * Request para cambiar rol de colaborador
 */
@Serializable
data class UpdateCollaboratorRoleRequest(
    val role: String
)
