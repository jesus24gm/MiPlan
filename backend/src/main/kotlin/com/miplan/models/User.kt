package com.miplan.models

import java.time.LocalDateTime

/**
 * Modelo de Usuario para respuestas (con rol como String)
 */
data class User(
    val id: Int,
    val email: String,
    val name: String,
    val role: String,
    val isVerified: Boolean,
    val createdAt: LocalDateTime
)
