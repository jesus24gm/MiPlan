package com.miplan.models.entities

import java.time.LocalDateTime

/**
 * Entidad de Usuario
 */
data class User(
    val id: Int,
    val email: String,
    val passwordHash: String,
    val name: String,
    val roleId: Int,
    val isVerified: Boolean,
    val verificationToken: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
