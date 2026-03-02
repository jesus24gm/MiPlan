package com.miplan.data.repository

import com.miplan.data.remote.ApiService
import com.miplan.data.remote.dto.response.ApiResponse
import com.miplan.domain.model.AdminStats
import com.miplan.domain.model.Role
import com.miplan.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository para funciones de administración
 */
@Singleton
class AdminRepository @Inject constructor(
    private val apiService: ApiService
) {
    /**
     * Obtiene la lista de todos los usuarios
     */
    suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val response = apiService.getAllUsers()
            if (response.success && response.data != null) {
                Result.success(response.data.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.message ?: "Error al obtener usuarios"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Cambia el rol de un usuario
     */
    suspend fun updateUserRole(userId: Int, role: Role): Result<User> {
        return try {
            val response = apiService.updateUserRole(userId, role.name)
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message ?: "Error al actualizar rol"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene estadísticas del sistema
     */
    suspend fun getAdminStats(): Result<AdminStats> {
        return try {
            val response = apiService.getAdminStats()
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message ?: "Error al obtener estadísticas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Suspende o activa una cuenta de usuario
     */
    suspend fun toggleUserStatus(userId: Int, isActive: Boolean): Result<User> {
        return try {
            val response = apiService.toggleUserStatus(userId, isActive)
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message ?: "Error al cambiar estado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Elimina un usuario del sistema
     */
    suspend fun deleteUser(userId: Int): Result<String> {
        return try {
            val response = apiService.deleteUser(userId)
            if (response.success) {
                Result.success(response.message)
            } else {
                Result.failure(Exception(response.message ?: "Error al eliminar usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
