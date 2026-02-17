package com.miplan.services

import com.miplan.models.entities.RoleType
import com.miplan.models.entities.User
import com.miplan.models.responses.AuthResponse
import com.miplan.models.responses.UserResponse
import com.miplan.repositories.UserRepository
import com.miplan.security.JwtConfig
import com.miplan.security.PasswordHasher
import java.time.format.DateTimeFormatter

/**
 * Servicio de autenticación
 */
class AuthService(
    private val userRepository: UserRepository,
    private val emailService: IEmailService,
    private val jwtConfig: JwtConfig
) {
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    /**
     * Registra un nuevo usuario
     */
    suspend fun register(email: String, password: String, name: String): String {
        // Validar que el email no exista
        val existingUser = userRepository.findByEmail(email)
        if (existingUser != null) {
            throw IllegalArgumentException("El email ya está registrado")
        }
        
        // Validar contraseña
        if (password.length < 6) {
            throw IllegalArgumentException("La contraseña debe tener al menos 6 caracteres")
        }
        
        // Hashear contraseña
        val passwordHash = PasswordHasher.hashPassword(password)
        
        // Crear usuario con rol USER por defecto y verificado automáticamente
        val user = userRepository.create(
            email = email,
            passwordHash = passwordHash,
            name = name,
            roleId = RoleType.USER.id,
            verificationToken = null
        ) ?: throw Exception("Error al crear usuario")
        
        // Marcar como verificado inmediatamente
        userRepository.verifyEmail(user.id)
        
        // No enviar email de verificación (desactivado temporalmente)
        // emailService.sendVerificationEmail(email, name, verificationToken)
        
        return "Usuario registrado exitosamente. Ya puedes iniciar sesión."
    }
    
    /**
     * Inicia sesión
     */
    suspend fun login(email: String, password: String): AuthResponse {
        // Buscar usuario
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("Credenciales inválidas")
        
        // Verificar contraseña
        if (!PasswordHasher.verifyPassword(password, user.passwordHash)) {
            throw IllegalArgumentException("Credenciales inválidas")
        }
        
        // Verificación de email desactivada temporalmente
        // if (!user.isVerified) {
        //     throw IllegalArgumentException("Por favor verifica tu email antes de iniciar sesión")
        // }
        
        // Obtener rol
        val roleName = userRepository.getUserRoleName(user.id)
            ?: throw Exception("Error al obtener rol del usuario")
        
        // Generar token JWT
        val token = jwtConfig.generateToken(user, roleName)
        
        return AuthResponse(
            token = token,
            user = user.toUserResponse(roleName)
        )
    }
    
    /**
     * Verifica el email de un usuario
     */
    suspend fun verifyEmail(token: String): String {
        val user = userRepository.findByVerificationToken(token)
            ?: throw IllegalArgumentException("Token de verificación inválido")
        
        if (user.isVerified) {
            return "El email ya ha sido verificado"
        }
        
        userRepository.verifyEmail(user.id)
        
        return "Email verificado exitosamente. Ya puedes iniciar sesión."
    }
    
    /**
     * Obtiene el usuario actual
     */
    suspend fun getCurrentUser(userId: Int): UserResponse {
        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("Usuario no encontrado")
        
        val roleName = userRepository.getUserRoleName(user.id)
            ?: throw Exception("Error al obtener rol del usuario")
        
        return user.toUserResponse(roleName)
    }
    
    /**
     * Convierte un User a UserResponse
     */
    private fun User.toUserResponse(roleName: String): UserResponse {
        return UserResponse(
            id = this.id,
            email = this.email,
            name = this.name,
            role = roleName,
            isVerified = this.isVerified,
            createdAt = this.createdAt.format(dateFormatter)
        )
    }
}
