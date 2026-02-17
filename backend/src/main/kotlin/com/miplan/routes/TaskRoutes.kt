package com.miplan.routes

import com.miplan.models.requests.CreateTaskRequest
import com.miplan.models.requests.UpdateTaskRequest
import com.miplan.models.requests.UpdateTaskStatusRequest
import com.miplan.models.responses.errorResponse
import com.miplan.models.responses.successResponse
import com.miplan.services.TaskService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Rutas de tareas
 */
fun Route.taskRoutes(taskService: TaskService) {
    
    authenticate("jwt") {
        route("/api/tasks") {
            
            // GET /api/tasks
            get {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    val tasks = taskService.getUserTasks(userId)
                    call.respond(HttpStatusCode.OK, successResponse("Tareas obtenidas", tasks))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse(e.message ?: "Error al obtener tareas"))
                }
            }
            
            // GET /api/tasks/{id}
            get("/{id}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    val taskId = call.parameters["id"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("ID de tarea inválido")
                    
                    val task = taskService.getTaskById(taskId, userId)
                    call.respond(HttpStatusCode.OK, successResponse("Tarea obtenida", task))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, errorResponse(e.message ?: "Error"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
            
            // GET /api/tasks/board/{boardId}
            get("/board/{boardId}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    val boardId = call.parameters["boardId"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("ID de tablero inválido")
                    
                    val tasks = taskService.getTasksByBoard(boardId, userId)
                    call.respond(HttpStatusCode.OK, successResponse("Tareas obtenidas", tasks))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, errorResponse(e.message ?: "Error"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
            
            // GET /api/tasks/status/{status}
            get("/status/{status}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    val status = call.parameters["status"]
                        ?: throw IllegalArgumentException("Estado no proporcionado")
                    
                    val tasks = taskService.getTasksByStatus(userId, status)
                    call.respond(HttpStatusCode.OK, successResponse("Tareas obtenidas", tasks))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, errorResponse(e.message ?: "Error"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
            
            // GET /api/tasks/date/{date}
            get("/date/{date}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    val date = call.parameters["date"]
                        ?: throw IllegalArgumentException("Fecha no proporcionada")
                    
                    val tasks = taskService.getTasksByDate(userId, date)
                    call.respond(HttpStatusCode.OK, successResponse("Tareas obtenidas", tasks))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, errorResponse(e.message ?: "Error"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
            
            // POST /api/tasks
            post {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    val request = call.receive<CreateTaskRequest>()
                    println("📝 Creando tarea para usuario $userId: ${request.title}")
                    
                    val task = taskService.createTask(
                        userId = userId,
                        title = request.title,
                        description = request.description,
                        priority = request.priority,
                        dueDateStr = request.dueDate,
                        boardId = request.boardId
                    )
                    println("✅ Tarea creada exitosamente: ${task.id}")
                    call.respond(HttpStatusCode.Created, successResponse("Tarea creada", task))
                } catch (e: IllegalArgumentException) {
                    println("❌ Error de validación al crear tarea: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.BadRequest, errorResponse(e.message ?: "Error al crear tarea"))
                } catch (e: Exception) {
                    println("❌ Error interno al crear tarea: ${e.message}")
                    e.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, errorResponse(e.message ?: "Error interno del servidor"))
                }
            }
            
            // PUT /api/tasks/{id}
            put("/{id}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    val taskId = call.parameters["id"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("ID de tarea inválido")
                    
                    val request = call.receive<UpdateTaskRequest>()
                    val task = taskService.updateTask(
                        taskId = taskId,
                        userId = userId,
                        title = request.title,
                        description = request.description,
                        status = request.status,
                        priority = request.priority,
                        dueDateStr = request.dueDate,
                        boardId = request.boardId
                    )
                    call.respond(HttpStatusCode.OK, successResponse("Tarea actualizada", task))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, errorResponse(e.message ?: "Error al actualizar tarea"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
            
            // PATCH /api/tasks/{id}/status
            patch("/{id}/status") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    val taskId = call.parameters["id"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("ID de tarea inválido")
                    
                    val request = call.receive<UpdateTaskStatusRequest>()
                    val task = taskService.updateTaskStatus(taskId, userId, request.status)
                    call.respond(HttpStatusCode.OK, successResponse("Estado actualizado", task))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, errorResponse(e.message ?: "Error"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
            
            // DELETE /api/tasks/{id}
            delete("/{id}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    val taskId = call.parameters["id"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("ID de tarea inválido")
                    
                    taskService.deleteTask(taskId, userId)
                    call.respond(HttpStatusCode.OK, successResponse<String>("Tarea eliminada", null))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, errorResponse(e.message ?: "Error"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
        }
    }
}
