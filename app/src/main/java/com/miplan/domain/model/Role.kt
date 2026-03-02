package com.miplan.domain.model

/**
 * Enum para roles de usuario
 */
enum class Role(val displayName: String) {
    USER("Usuario"),
    ADMIN("Administrador");

    companion object {
        fun fromString(value: String): Role {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: USER
        }
    }
}
