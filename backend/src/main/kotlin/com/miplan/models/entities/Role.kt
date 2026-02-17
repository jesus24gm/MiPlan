package com.miplan.models.entities

/**
 * Entidad de Rol
 */
data class Role(
    val id: Int,
    val name: String,
    val description: String?
)

/**
 * Enum de roles predefinidos
 */
enum class RoleType(val id: Int, val roleName: String) {
    USER(1, "USER"),
    ADMIN(2, "ADMIN");
    
    companion object {
        fun fromId(id: Int): RoleType? = values().find { it.id == id }
        fun fromName(name: String): RoleType? = values().find { it.roleName.equals(name, ignoreCase = true) }
    }
}
