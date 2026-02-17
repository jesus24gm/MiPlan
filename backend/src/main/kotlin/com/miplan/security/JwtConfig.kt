package com.miplan.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.miplan.models.entities.User
import io.ktor.server.config.*
import java.util.*

/**
 * Configuración y utilidades para JWT
 */
class JwtConfig(config: ApplicationConfig) {
    
    private val secret = config.property("jwt.secret").getString()
    private val issuer = config.property("jwt.issuer").getString()
    private val audience = config.property("jwt.audience").getString()
    val realm = config.property("jwt.realm").getString()
    private val validityInMs = config.property("jwt.validityInMs").getString().toLong()
    
    private val algorithm = Algorithm.HMAC256(secret)
    
    /**
     * Genera un token JWT para un usuario
     */
    fun generateToken(user: User, roleName: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", user.id)
            .withClaim("email", user.email)
            .withClaim("role", roleName)
            .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
            .sign(algorithm)
    }
    
    /**
     * Verifica un token JWT
     */
    fun verifyToken(token: String): DecodedJWT {
        val verifier = JWT.require(algorithm)
            .withAudience(audience)
            .withIssuer(issuer)
            .build()
        
        return verifier.verify(token)
    }
    
    /**
     * Extrae el ID del usuario del token
     */
    fun getUserIdFromToken(token: String): Int {
        val decoded = verifyToken(token)
        return decoded.getClaim("userId").asInt()
    }
    
    /**
     * Extrae el rol del usuario del token
     */
    fun getRoleFromToken(token: String): String {
        val decoded = verifyToken(token)
        return decoded.getClaim("role").asString()
    }
    
    /**
     * Extrae el email del usuario del token
     */
    fun getEmailFromToken(token: String): String {
        val decoded = verifyToken(token)
        return decoded.getClaim("email").asString()
    }
}
