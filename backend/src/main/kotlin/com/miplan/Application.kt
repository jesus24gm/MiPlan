package com.miplan

import com.miplan.database.DatabaseFactory
import com.miplan.plugins.*
import com.miplan.repositories.TaskRepository
import com.miplan.repositories.UserRepository
import com.miplan.security.JwtConfig
import com.miplan.services.AuthService
import com.miplan.services.EmailService
import com.miplan.services.ResendEmailService
import com.miplan.services.TaskService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

/**
 * Punto de entrada de la aplicación
 */
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

/**
 * Módulo principal de la aplicación
 */
fun Application.module() {
    // Inicializar base de datos
    DatabaseFactory.init(environment.config)
    
    // Inicializar servicios
    val jwtConfig = JwtConfig(environment.config)
    // Usar ResendEmailService si existe RESEND_API_KEY, sino usar EmailService (SMTP)
    val emailService: IEmailService = if (environment.config.propertyOrNull("email.resend_api_key") != null) {
        ResendEmailService(environment.config)
    } else {
        EmailService(environment.config)
    }
    
    // Inicializar repositorios
    val userRepository = UserRepository()
    val taskRepository = TaskRepository()
    
    // Inicializar servicios de negocio
    val authService = AuthService(userRepository, emailService, jwtConfig)
    val taskService = TaskService(taskRepository)
    
    // Configurar plugins
    configureSerialization()
    configureCORS()
    configureStatusPages()
    configureSecurity(jwtConfig)
    configureRouting(authService, taskService)
    
    log.info("MiPlan Backend iniciado correctamente")
}
