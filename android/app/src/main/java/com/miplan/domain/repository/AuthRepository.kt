package com.miplan.domain.repository

import com.miplan.domain.model.User

/**
 * Interface del repositorio de autenticación
 * Define las operaciones de autenticación sin implementación
 */
interface AuthRepository {
    
    /**
     * Registra un nuevo usuario
     */
    suspend fun register(
        email: String,
        password: String,
        name: String
    ): Result<String>
    
    /**
     * Inicia sesión con email y contraseña
     */
    suspend fun login(
        email: String,
        password: String
    ): Result<Pair<String, User>>
    
    /**
     * Cierra la sesión del usuario actual
     */
    suspend fun logout(): Result<Unit>
    
    /**
     * Verifica el email con el token recibido
     */
    suspend fun verifyEmail(token: String): Result<String>
    
    /**
     * Obtiene el usuario actual autenticado
     */
    suspend fun getCurrentUser(): Result<User>
    
    /**
     * Verifica si hay un usuario autenticado
     */
    suspend fun isAuthenticated(): Boolean
    
    /**
     * Obtiene el token JWT almacenado
     */
    suspend fun getToken(): String?
}
