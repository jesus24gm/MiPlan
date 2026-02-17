package com.miplan.security

import org.mindrot.jbcrypt.BCrypt

/**
 * Utilidad para hashear y verificar contraseñas con BCrypt
 */
object PasswordHasher {
    
    /**
     * Hashea una contraseña usando BCrypt
     */
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(12))
    }
    
    /**
     * Verifica si una contraseña coincide con su hash
     */
    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return try {
            BCrypt.checkpw(password, hashedPassword)
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Genera un token aleatorio para verificación de email
     */
    fun generateVerificationToken(): String {
        return java.util.UUID.randomUUID().toString()
    }
}
