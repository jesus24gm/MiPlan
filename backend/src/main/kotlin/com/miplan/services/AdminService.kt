package com.miplan.services

import com.miplan.database.DatabaseFactory.dbQuery
import com.miplan.models.User
import com.miplan.models.responses.AdminStatsResponse
import com.miplan.models.responses.UserResponse
import com.miplan.repositories.BoardRepository
import com.miplan.repositories.TaskRepository
import com.miplan.repositories.UserRepository

/**
 * Servicio para funciones de administración del sistema
 */
class AdminService(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val boardRepository: BoardRepository
) {
    
    /**
     * Obtiene todos los usuarios del sistema
     */
    suspend fun getAllUsers(): List<UserResponse> = dbQuery {
        userRepository.getAllUsers().map { it.toResponse() }
    }
    
    /**
     * Actualiza el rol de un usuario
     */
    suspend fun updateUserRole(userId: Int, role: String): UserResponse {
        // Validar que el rol sea válido
        if (role != "USER" && role != "ADMIN") {
            throw IllegalArgumentException("Rol inválido. Debe ser USER o ADMIN")
        }
        
        val user = userRepository.getUserById(userId)
            ?: throw IllegalArgumentException("Usuario no encontrado")
        
        userRepository.updateRole(userId, role)
        
        val updatedUser = userRepository.getUserById(userId)
            ?: throw IllegalArgumentException("Error al actualizar rol")
        
        return updatedUser.toResponse()
    }
    
    /**
     * Obtiene estadísticas del sistema
     */
    suspend fun getAdminStats(): AdminStatsResponse = dbQuery {
        val totalUsers = userRepository.countUsers()
        val activeUsers = userRepository.countActiveUsers()
        val totalTasks = taskRepository.countTasks()
        val completedTasks = taskRepository.countCompletedTasks()
        val pendingTasks = taskRepository.countPendingTasks()
        val totalBoards = boardRepository.countBoards()
        
        AdminStatsResponse(
            totalUsers = totalUsers,
            activeUsers = activeUsers,
            totalTasks = totalTasks,
            completedTasks = completedTasks,
            pendingTasks = pendingTasks,
            totalBoards = totalBoards
        )
    }
    
    /**
     * Activa o suspende una cuenta de usuario
     */
    suspend fun toggleUserStatus(userId: Int, isActive: Boolean): UserResponse {
        val user = userRepository.getUserById(userId)
            ?: throw IllegalArgumentException("Usuario no encontrado")
        
        userRepository.updateStatus(userId, isActive)
        
        val updatedUser = userRepository.getUserById(userId)
            ?: throw IllegalArgumentException("Error al actualizar estado")
        
        return updatedUser.toResponse()
    }
    
    /**
     * Elimina un usuario del sistema
     */
    suspend fun deleteUser(userId: Int): Boolean {
        val user = userRepository.getUserById(userId)
            ?: throw IllegalArgumentException("Usuario no encontrado")
        
        return userRepository.delete(userId)
    }
}

/**
 * Extensión para convertir User a UserResponse
 */
private fun User.toResponse(): UserResponse {
    return UserResponse(
        id = this.id,
        email = this.email,
        name = this.name,
        role = this.role,
        isVerified = this.isVerified,
        avatarUrl = this.avatarUrl,
        createdAt = this.createdAt.toString()
    )
}
