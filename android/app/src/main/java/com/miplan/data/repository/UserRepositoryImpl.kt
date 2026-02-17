package com.miplan.data.repository

import com.miplan.data.remote.ApiService
import com.miplan.domain.model.Role
import com.miplan.domain.model.User
import com.miplan.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio de usuarios
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserRepository {
    
    override suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val response = apiService.getAllUsers()
            
            if (response.success && response.data != null) {
                val users = response.data.map { it.toDomain() }
                Result.success(users)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserById(id: Int): Result<User> {
        return try {
            val response = apiService.getUserById(id)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateProfile(name: String, email: String): Result<User> {
        return try {
            val response = apiService.updateProfile(name, email)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun changeUserRole(userId: Int, role: Role): Result<Unit> {
        return try {
            val response = apiService.changeUserRole(userId, role.name)
            
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteUser(userId: Int): Result<Unit> {
        return try {
            val response = apiService.deleteUser(userId)
            
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
