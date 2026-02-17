package com.miplan.models.responses

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserResponse
)

@Serializable
data class UserResponse(
    val id: Int,
    val email: String,
    val name: String,
    val role: String,
    val isVerified: Boolean,
    val createdAt: String
)
