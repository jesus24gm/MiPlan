package com.miplan.models.requests

import kotlinx.serialization.Serializable

/**
 * Request para actualizar el perfil del usuario
 */
@Serializable
data class UpdateProfileRequest(
    val name: String,
    val email: String
)

/**
 * Request para cambiar la contraseña
 */
@Serializable
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)
