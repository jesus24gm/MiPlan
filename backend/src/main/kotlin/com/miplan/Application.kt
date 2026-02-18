package com.miplan

import com.miplan.database.DatabaseFactory
import com.miplan.database.Migrations
import com.miplan.plugins.*
import com.miplan.repositories.TaskRepository
import com.miplan.repositories.UserRepository
import com.miplan.security.JwtConfig
import com.miplan.services.AuthService
import com.miplan.services.EmailService
import com.miplan.services.IEmailService
import com.miplan.services.MailtrapEmailService
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
    
    // Inicializar servicios de negocio
    val authService = AuthService(userRepository, emailService, jwtConfig)
    val taskService = TaskService(taskRepository)
    
    // Configurar plugins
    configureSerialization()
    configureCORS()
    configureStatusPages()
    configureSecurity(jwtConfig)
    configureRouting(authService, taskService)
    
    log.info("MiPlan Backend iniciado correctamente - v2.0.0 con Sistema Kanban")
    println("✅ Backend v2.0.0 - Sistema Kanban: Boards, Columns, Cards, Checklists, Attachments")
    println("📋 Nuevas tablas: columns, cards, card_checklists, checklist_items, card_attachments")
}
