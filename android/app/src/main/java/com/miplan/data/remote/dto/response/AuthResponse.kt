package com.miplan.data.remote.dto.response

import com.miplan.domain.model.Role
import com.miplan.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserDto
)

@Serializable
data class UserDto(
    val id: Int,
    val email: String,
    val name: String,
    val role: String,
    val isVerified: Boolean,
    val createdAt: String
) {
    fun toDomain(): User {
        return User(
            id = id,
            email = email,
            name = name,
            role = Role.fromString(role),
            isVerified = isVerified,
            createdAt = createdAt
        )
    }
}
