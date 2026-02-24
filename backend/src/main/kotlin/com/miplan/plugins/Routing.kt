package com.miplan.plugins

import com.miplan.routes.*
import com.miplan.services.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Configuración de rutas
 */
fun Application.configureRouting(
    authService: AuthService,
    userService: UserService,
    taskService: TaskService,
    boardService: BoardService,
    columnService: ColumnService,
    cardService: CardService,
    checklistService: ChecklistService,
    attachmentService: AttachmentService
) {
    routing {
        get("/") {
            call.respondText("MiPlan API v2.0.0 - Sistema Kanban")
        }
        
        get("/health") {
            call.respondText("OK")
        }
        
        migrationRoutes()
        authRoutes(authService)
        userRoutes(userService)
        taskRoutes(taskService)
        boardRoutes(boardService)
        kanbanRoutes(columnService, cardService, checklistService, attachmentService, boardService)
    }
}
