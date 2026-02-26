package com.miplan.services

import com.miplan.models.responses.UserResponse
import com.miplan.models.responses.UserStatsResponse
import com.miplan.repositories.BoardRepository
import com.miplan.repositories.TaskRepository
import com.miplan.repositories.UserRepository
import com.miplan.security.PasswordHasher
import java.time.format.DateTimeFormatter

/**
 * Servicio de gestión de usuarios
 */
class UserService(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val boardRepository: BoardRepository
) {
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    /**
     * Obtiene un usuario por ID
     */
    suspend fun getUserById(userId: Int): UserResponse {
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("Usuario no encontrado")
        
        val roleName = userRepository.getUserRoleName(userId) ?: "USER"
        
        return UserResponse(
            id = user.id,
            email = user.email,
            name = user.name,
            role = roleName,
            isVerified = user.isVerified,
            avatarUrl = user.avatarUrl,
            createdAt = user.createdAt.format(dateFormatter)
        )
    }
    
    /**
     * Actualiza el perfil del usuario
     */
    suspend fun updateProfile(userId: Int, name: String, email: String): UserResponse {
        // Validar que el usuario existe
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("Usuario no encontrado")
        
        // Validar que el email no esté en uso por otro usuario
        if (email != user.email) {
            val existingUser = userRepository.findByEmail(email)
            if (existingUser != null && existingUser.id != userId) {
                throw IllegalArgumentException("El email ya está en uso")
            }
        }
        
        // Validar nombre
        if (name.isBlank()) {
            throw IllegalArgumentException("El nombre no puede estar vacío")
        }
        
        // Actualizar usuario
        val updated = userRepository.updateProfile(userId, name, email)
        if (!updated) {
            throw Exception("Error al actualizar perfil")
        }
        
        // Obtener usuario actualizado
        val updatedUser = userRepository.findById(userId)
            ?: throw Exception("Error al obtener usuario actualizado")
        
        val roleName = userRepository.getUserRoleName(userId) ?: "USER"
        
        return UserResponse(
            id = updatedUser.id,
            email = updatedUser.email,
            name = updatedUser.name,
            role = roleName,
            isVerified = updatedUser.isVerified,
            avatarUrl = updatedUser.avatarUrl,
            createdAt = updatedUser.createdAt.format(dateFormatter)
        )
    }
    
    /**
     * Actualiza el avatar del usuario
     */
    suspend fun updateAvatar(userId: Int, avatarUrl: String): UserResponse {
        // Validar que el usuario existe
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("Usuario no encontrado")
        
        // Actualizar avatar
        val updated = userRepository.updateAvatar(userId, avatarUrl)
        if (!updated) {
            throw Exception("Error al actualizar avatar")
        }
        
        // Obtener usuario actualizado
        val updatedUser = userRepository.findById(userId)
            ?: throw Exception("Error al obtener usuario actualizado")
        
        val roleName = userRepository.getUserRoleName(userId) ?: "USER"
        
        return UserResponse(
            id = updatedUser.id,
            email = updatedUser.email,
            name = updatedUser.name,
            role = roleName,
            isVerified = updatedUser.isVerified,
            avatarUrl = updatedUser.avatarUrl,
            createdAt = updatedUser.createdAt.format(dateFormatter)
        )
    }
    
    /**
     * Cambia la contraseña del usuario
     */
    suspend fun changePassword(userId: Int, currentPassword: String, newPassword: String) {
        // Validar que el usuario existe
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("Usuario no encontrado")
        
        // Verificar contraseña actual
        if (!PasswordHasher.verifyPassword(currentPassword, user.passwordHash)) {
            throw IllegalArgumentException("La contraseña actual es incorrecta")
        }
        
        // Validar nueva contraseña
        if (newPassword.length < 6) {
            throw IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres")
        }
        
        // Hashear nueva contraseña
        val newPasswordHash = PasswordHasher.hashPassword(newPassword)
        
        // Actualizar contraseña
        val updated = userRepository.updatePassword(userId, newPasswordHash)
        if (!updated) {
            throw Exception("Error al cambiar contraseña")
        }
    }
    
    /**
     * Elimina la cuenta del usuario
     */
    suspend fun deleteAccount(userId: Int) {
        // Validar que el usuario existe
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("Usuario no encontrado")
        
        // Eliminar todas las tareas del usuario
        val userTasks = taskRepository.findByUserId(userId)
        userTasks.forEach { task ->
            taskRepository.delete(task.id)
        }
        
        // Eliminar todos los tableros del usuario
        val userBoards = boardRepository.findByUserId(userId)
        userBoards.forEach { board ->
            boardRepository.delete(board.id)
        }
        
        // Eliminar usuario
        val deleted = userRepository.delete(userId)
        if (!deleted) {
            throw Exception("Error al eliminar cuenta")
        }
    }
    
    /**
     * Obtiene las estadísticas del usuario
     */
    suspend fun getUserStats(userId: Int): UserStatsResponse {
        // Validar que el usuario existe
        userRepository.findById(userId)
            ?: throw IllegalArgumentException("Usuario no encontrado")
        
        // Obtener todas las tareas del usuario
        val tasks = taskRepository.findByUserId(userId)
        
        // Calcular estadísticas de tareas
        val totalTasks = tasks.size
        val completedTasks = tasks.count { it.status == "COMPLETED" }
        val pendingTasks = tasks.count { it.status == "PENDING" }
        
        // Obtener todos los tableros del usuario
        val boards = boardRepository.findByUserId(userId)
        val totalBoards = boards.size
        
        // Calcular tableros activos (que tienen al menos una columna o tarjeta)
        // Por ahora consideramos todos los tableros como activos
        val activeBoards = totalBoards
        
        // Calcular tasa de completado
        val completionRate = if (totalTasks > 0) {
            (completedTasks * 100) / totalTasks
        } else {
            0
        }
        
        return UserStatsResponse(
            totalTasks = totalTasks,
            completedTasks = completedTasks,
            pendingTasks = pendingTasks,
            totalBoards = totalBoards,
            activeBoards = activeBoards,
            completionRate = completionRate
        )
    }
}
