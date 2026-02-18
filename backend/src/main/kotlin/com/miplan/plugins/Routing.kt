package com.miplan.plugins

import com.miplan.routes.authRoutes
import com.miplan.routes.migrationRoutes
import com.miplan.routes.taskRoutes
import com.miplan.services.AuthService
import com.miplan.services.TaskService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Configuración de rutas
 */
fun Application.configureRouting(
    authService: AuthService,
    taskService: TaskService
) {
    routing {
        get("/") {
            call.respondText("MiPlan API v1.0.0")
        }
        
        get("/health") {
            call.respondText("OK")
        }
        
        migrationRoutes()
        authRoutes(authService)
        taskRoutes(taskService)
    }
}
