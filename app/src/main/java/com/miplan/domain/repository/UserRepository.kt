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
     * Actualiza el avatar del usuario actual
     */
    suspend fun updateAvatar(avatarUrl: String): Result<User>
    
    /**
     * Cambia la contraseña del usuario actual
     */
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit>
    
    /**
     * Elimina la cuenta del usuario actual
     */
    suspend fun deleteAccount(): Result<Unit>
    
    /**
     * Actualiza el token FCM del usuario
     */
    suspend fun updateFcmToken(token: String): Result<Unit>
    
    /**
     * Obtiene estadísticas del usuario actual
     */
    suspend fun getUserStats(): Result<UserStats>
    
    /**
     * Cambia el rol de un usuario (solo admin)
     */
    suspend fun changeUserRole(userId: Int, role: Role): Result<Unit>
    
    /**
     * Elimina un usuario (solo admin)
     */
    suspend fun deleteUser(userId: Int): Result<Unit>
}

/**
 * Estadísticas del usuario
 */
data class UserStats(
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingTasks: Int,
    val totalBoards: Int,
    val activeBoards: Int,
    val completionRate: Int
)
