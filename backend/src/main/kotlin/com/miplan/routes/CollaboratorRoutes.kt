package com.miplan.routes

import com.miplan.models.entities.CollaboratorRole
import com.miplan.models.responses.AddCollaboratorRequest
import com.miplan.models.responses.ApiResponse
import com.miplan.models.responses.UpdateCollaboratorRoleRequest
import com.miplan.services.CollaboratorService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Rutas para gestión de colaboradores de tareas
 */
fun Route.collaboratorRoutes(collaboratorService: CollaboratorService) {
    
    route("/api/tasks/{taskId}/collaborators") {
        authenticate {
            
            /**
             * GET /api/tasks/:taskId/collaborators
             * Obtiene todos los colaboradores de una tarea
             */
            get {
                try {
                    val taskId = call.parameters["taskId"]?.toIntOrNull()
                        ?: return@get call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<Unit>(success = false, message = "ID de tarea inválido")
                        )
                    
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: return@get call.respond(
                            HttpStatusCode.Unauthorized,
                            ApiResponse<Unit>(success = false, message = "No autenticado")
                        )
                    
                    val result = collaboratorService.getCollaborators(taskId, userId)
                    
                    result.fold(
                        onSuccess = { collaborators ->
                            call.respond(
                                HttpStatusCode.OK,
                                ApiResponse(
                                    success = true,
                                    message = "Colaboradores obtenidos",
                                    data = collaborators
                                )
                            )
                        },
                        onFailure = { error ->
                            call.respond(
                                HttpStatusCode.Forbidden,
                                ApiResponse<Unit>(success = false, message = error.message ?: "Error al obtener colaboradores")
                            )
                        }
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Unit>(success = false, message = "Error del servidor: ${e.message}")
                    )
                }
            }
            
            /**
             * POST /api/tasks/:taskId/collaborators
             * Agrega un colaborador a una tarea
             */
            post {
                try {
                    val taskId = call.parameters["taskId"]?.toIntOrNull()
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<Unit>(success = false, message = "ID de tarea inválido")
                        )
                    
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: return@post call.respond(
                            HttpStatusCode.Unauthorized,
                            ApiResponse<Unit>(success = false, message = "No autenticado")
                        )
                    
                    val request = call.receive<AddCollaboratorRequest>()
                    
                    // Validar rol
                    val role = try {
                        CollaboratorRole.valueOf(request.role.uppercase())
                    } catch (e: Exception) {
                        return@post call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<Unit>(success = false, message = "Rol inválido. Debe ser OWNER, EDITOR o VIEWER")
                        )
                    }
                    
                    val result = collaboratorService.addCollaborator(
                        taskId = taskId,
                        userEmail = request.userEmail,
                        role = role,
                        addedByUserId = userId
                    )
                    
                    result.fold(
                        onSuccess = { collaborator ->
                            call.respond(
                                HttpStatusCode.Created,
                                ApiResponse(
                                    success = true,
                                    message = "Colaborador agregado exitosamente",
                                    data = collaborator
                                )
                            )
                        },
                        onFailure = { error ->
                            call.respond(
                                HttpStatusCode.BadRequest,
                                ApiResponse<Unit>(success = false, message = error.message ?: "Error al agregar colaborador")
                            )
                        }
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Unit>(success = false, message = "Error del servidor: ${e.message}")
                    )
                }
            }
            
            /**
             * PUT /api/tasks/:taskId/collaborators/:userId/role
             * Actualiza el rol de un colaborador
             */
            put("/{userId}/role") {
                try {
                    val taskId = call.parameters["taskId"]?.toIntOrNull()
                        ?: return@put call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<Unit>(success = false, message = "ID de tarea inválido")
                        )
                    
                    val collaboratorUserId = call.parameters["userId"]?.toIntOrNull()
                        ?: return@put call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<Unit>(success = false, message = "ID de usuario inválido")
                        )
                    
                    val principal = call.principal<JWTPrincipal>()
                    val requestUserId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: return@put call.respond(
                            HttpStatusCode.Unauthorized,
                            ApiResponse<Unit>(success = false, message = "No autenticado")
                        )
                    
                    val request = call.receive<UpdateCollaboratorRoleRequest>()
                    
                    // Validar rol
                    val newRole = try {
                        CollaboratorRole.valueOf(request.role.uppercase())
                    } catch (e: Exception) {
                        return@put call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<Unit>(success = false, message = "Rol inválido. Debe ser OWNER, EDITOR o VIEWER")
                        )
                    }
                    
                    val result = collaboratorService.updateCollaboratorRole(
                        taskId = taskId,
                        collaboratorUserId = collaboratorUserId,
                        newRole = newRole,
                        requestUserId = requestUserId
                    )
                    
                    result.fold(
                        onSuccess = {
                            call.respond(
                                HttpStatusCode.OK,
                                ApiResponse(
                                    success = true,
                                    message = "Rol actualizado exitosamente",
                                    data = "OK"
                                )
                            )
                        },
                        onFailure = { error ->
                            call.respond(
                                HttpStatusCode.BadRequest,
                                ApiResponse<Unit>(success = false, message = error.message ?: "Error al actualizar rol")
                            )
                        }
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Unit>(success = false, message = "Error del servidor: ${e.message}")
                    )
                }
            }
            
            /**
             * DELETE /api/tasks/:taskId/collaborators/:userId
             * Elimina un colaborador de una tarea
             */
            delete("/{userId}") {
                try {
                    val taskId = call.parameters["taskId"]?.toIntOrNull()
                        ?: return@delete call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<Unit>(success = false, message = "ID de tarea inválido")
                        )
                    
                    val collaboratorUserId = call.parameters["userId"]?.toIntOrNull()
                        ?: return@delete call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<Unit>(success = false, message = "ID de usuario inválido")
                        )
                    
                    val principal = call.principal<JWTPrincipal>()
                    val requestUserId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: return@delete call.respond(
                            HttpStatusCode.Unauthorized,
                            ApiResponse<Unit>(success = false, message = "No autenticado")
                        )
                    
                    val result = collaboratorService.removeCollaborator(
                        taskId = taskId,
                        collaboratorUserId = collaboratorUserId,
                        requestUserId = requestUserId
                    )
                    
                    result.fold(
                        onSuccess = {
                            call.respond(
                                HttpStatusCode.OK,
                                ApiResponse(
                                    success = true,
                                    message = "Colaborador eliminado exitosamente",
                                    data = "OK"
                                )
                            )
                        },
                        onFailure = { error ->
                            call.respond(
                                HttpStatusCode.BadRequest,
                                ApiResponse<Unit>(success = false, message = error.message ?: "Error al eliminar colaborador")
                            )
                        }
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Unit>(success = false, message = "Error del servidor: ${e.message}")
                    )
                }
            }
        }
    }
    
    /**
     * GET /api/tasks/shared
     * Obtiene las tareas compartidas con el usuario actual
     */
    route("/api/tasks/shared") {
        authenticate {
            get {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: return@get call.respond(
                            HttpStatusCode.Unauthorized,
                            ApiResponse<Unit>(success = false, message = "No autenticado")
                        )
                    
                    val result = collaboratorService.getSharedTasks(userId)
                    
                    result.fold(
                        onSuccess = { taskIds ->
                            call.respond(
                                HttpStatusCode.OK,
                                ApiResponse(
                                    success = true,
                                    message = "Tareas compartidas obtenidas",
                                    data = taskIds
                                )
                            )
                        },
                        onFailure = { error ->
                            call.respond(
                                HttpStatusCode.InternalServerError,
                                ApiResponse<Unit>(success = false, message = error.message ?: "Error al obtener tareas compartidas")
                            )
                        }
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Unit>(success = false, message = "Error del servidor: ${e.message}")
                    )
                }
            }
        }
    }
}
