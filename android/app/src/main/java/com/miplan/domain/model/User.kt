package com.miplan.domain.model

/**
 * Modelo de dominio para Usuario
 */
data class User(
    val id: Int,
    val email: String,
    val name: String,
    val role: Role,
    val isVerified: Boolean,
    val createdAt: String
)
