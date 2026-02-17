package com.miplan.domain.repository

import com.miplan.domain.model.Role
import com.miplan.domain.model.User

/**
 * Interface del repositorio de usuarios (principalmente para admin)
 */
interface UserRepository {
    
    /**
     * Obtiene todos los usuarios (solo admin)
     */
    suspend fun getAllUsers(): Result<List<User>>
    
    /**
     * Obtiene un usuario por ID
     */
    suspend fun getUserById(id: Int): Result<User>
    
    /**
     * Actualiza el perfil del usuario actual
     */
    suspend fun updateProfile(
        name: String,
        email: String
    ): Result<User>
    
    /**
     * Cambia el rol de un usuario (solo admin)
     */
    suspend fun changeUserRole(userId: Int, role: Role): Result<Unit>
    
    /**
     * Elimina un usuario (solo admin)
     */
    suspend fun deleteUser(userId: Int): Result<Unit>
}
