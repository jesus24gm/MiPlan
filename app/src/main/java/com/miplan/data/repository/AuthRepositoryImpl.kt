package com.miplan.data.repository

import com.miplan.data.local.TokenManager
import com.miplan.data.remote.ApiService
import com.miplan.data.remote.dto.request.LoginRequest
import com.miplan.data.remote.dto.request.RegisterRequest
import com.miplan.domain.model.User
import com.miplan.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio de autenticación
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : AuthRepository {
    
    override suspend fun register(
        email: String,
        password: String,
        name: String
    ): Result<String> {
        return try {
            val request = RegisterRequest(email, password, name)
            val response = apiService.register(request)
            
            if (response.success) {
                Result.success(response.message)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun login(
        email: String,
        password: String,
        rememberMe: Boolean
    ): Result<Pair<String, User>> {
        return try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)
            
            if (response.success && response.data != null) {
                val authData = response.data
                val user = authData.user.toDomain()
                
                // Guardar token y datos del usuario
                tokenManager.saveToken(authData.token)
                tokenManager.saveUserInfo(
                    userId = user.id,
                    email = user.email,
                    name = user.name,
                    role = user.role.name
                )
                
                // Guardar preferencia de auto-login
                tokenManager.setAutoLogin(rememberMe)
                
                Result.success(Pair(authData.token, user))
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logout(): Result<Unit> {
        return try {
            apiService.logout()
            tokenManager.clearToken() // Limpia token y deshabilita auto-login
            Result.success(Unit)
        } catch (e: Exception) {
            // Limpiar datos locales aunque falle la petición
            tokenManager.clearToken()
            Result.success(Unit)
        }
    }
    
    override suspend fun verifyEmail(token: String): Result<String> {
        return try {
            val response = apiService.verifyEmail(token)
            
            if (response.success) {
                Result.success(response.message)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCurrentUser(): Result<User> {
        return try {
            val response = apiService.getCurrentUser()
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun isAuthenticated(): Boolean {
        // Solo está autenticado si tiene token Y tiene auto-login habilitado
        return tokenManager.hasToken() && tokenManager.isAutoLoginEnabled()
    }
    
    override suspend fun getToken(): String? {
        return tokenManager.getToken()
    }
}
