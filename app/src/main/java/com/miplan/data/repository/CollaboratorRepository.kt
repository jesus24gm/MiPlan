package com.miplan.data.repository

import com.miplan.data.remote.ApiService
import com.miplan.data.remote.dto.response.AddCollaboratorRequest
import com.miplan.data.remote.dto.response.UpdateCollaboratorRoleRequest
import com.miplan.domain.model.CollaboratorRole
import com.miplan.domain.model.TaskCollaborator
import com.miplan.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para gestión de colaboradores
 */
@Singleton
class CollaboratorRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    /**
     * Obtiene los colaboradores de una tarea
     */
    suspend fun getTaskCollaborators(taskId: Int): Result<List<TaskCollaborator>> {
        return try {
            val response = apiService.getTaskCollaborators(taskId)
            
            if (response.success && response.data != null) {
                val collaborators = response.data.map { it.toDomain() }
                Result.success(collaborators)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Agrega un colaborador a una tarea
     */
    suspend fun addCollaborator(
        taskId: Int,
        userEmail: String,
        role: CollaboratorRole
    ): Result<TaskCollaborator> {
        return try {
            val request = AddCollaboratorRequest(
                userEmail = userEmail,
                role = role.name
            )
            val response = apiService.addCollaborator(taskId, request)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Actualiza el rol de un colaborador
     */
    suspend fun updateCollaboratorRole(
        taskId: Int,
        userId: Int,
        newRole: CollaboratorRole
    ): Result<Unit> {
        return try {
            val request = UpdateCollaboratorRoleRequest(role = newRole.name)
            val response = apiService.updateCollaboratorRole(taskId, userId, request)
            
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Elimina un colaborador de una tarea
     */
    suspend fun removeCollaborator(taskId: Int, userId: Int): Result<Unit> {
        return try {
            val response = apiService.removeCollaborator(taskId, userId)
            
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene las tareas compartidas con el usuario actual
     */
    suspend fun getSharedTasks(): Result<List<Int>> {
        return try {
            val response = apiService.getSharedTasks()
            
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Busca un usuario por email
     */
    suspend fun searchUserByEmail(email: String): Result<User?> {
        return try {
            val response = apiService.searchUserByEmail(email)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else if (response.message.contains("no encontrado", ignoreCase = true)) {
                Result.success(null)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
