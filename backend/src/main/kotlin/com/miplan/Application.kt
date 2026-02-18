package com.miplan

import com.miplan.database.DatabaseFactory
import com.miplan.database.Migrations
import com.miplan.plugins.*
import com.miplan.repositories.*
import com.miplan.security.JwtConfig
import com.miplan.services.*
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
    
    // Ejecutar migraciones
    Migrations.runMigrations()
    
    // Inicializar servicios
    val jwtConfig = JwtConfig(environment.config)
    // Detectar qué servicio de email usar
    val emailService: IEmailService = when {
        environment.config.propertyOrNull("mailtrap.api_token") != null || 
        environment.config.propertyOrNull("email.mailtrap_api_token") != null -> {
            MailtrapEmailService(environment.config)
        }
        environment.config.propertyOrNull("email.resend_api_key") != null -> {
            ResendEmailService(environment.config)
        }
        else -> {
            EmailService(environment.config)
        }
    }
    
    // Inicializar repositorios
    val userRepository = UserRepository()
    val taskRepository = TaskRepository()
    val boardRepository = BoardRepository()
    val columnRepository = ColumnRepository()
    val cardRepository = CardRepository()
    val checklistRepository = ChecklistRepository()
    val attachmentRepository = AttachmentRepository()
    
    // Inicializar servicios de negocio
    val authService = AuthService(userRepository, emailService, jwtConfig)
    val taskService = TaskService(taskRepository)
    val boardService = BoardService(boardRepository, columnRepository, cardRepository)
    val columnService = ColumnService(columnRepository)
    val cardService = CardService(cardRepository, checklistRepository, attachmentRepository)
    val checklistService = ChecklistService(checklistRepository)
    val attachmentService = AttachmentService(attachmentRepository)
    
    // Configurar plugins
    configureSerialization()
    configureCORS()
    configureStatusPages()
    configureSecurity(jwtConfig)
    configureRouting(authService, taskService, boardService, columnService, cardService, checklistService, attachmentService)
}
