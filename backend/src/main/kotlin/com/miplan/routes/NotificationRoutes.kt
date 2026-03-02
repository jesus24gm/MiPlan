package com.miplan.routes

import com.miplan.models.responses.errorResponse
import com.miplan.models.responses.successResponse
import com.miplan.services.NotificationService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Rutas de notificaciones
 */
fun Route.notificationRoutes(notificationService: NotificationService) {
    
    authenticate("jwt") {
        route("/api/notifications") {
            
            // GET /api/notifications - Obtener todas las notificaciones del usuario
            get {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    val notifications = notificationService.getUserNotifications(userId)
                    call.respond(HttpStatusCode.OK, successResponse("Notificaciones obtenidas", notifications))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.Unauthorized, errorResponse(e.message ?: "No autorizado"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
            
            // GET /api/notifications/unread - Obtener notificaciones no leídas
            get("/unread") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    val notifications = notificationService.getUnreadNotifications(userId)
                    call.respond(HttpStatusCode.OK, successResponse("Notificaciones no leídas obtenidas", notifications))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.Unauthorized, errorResponse(e.message ?: "No autorizado"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
            
            // PUT /api/notifications/{id}/read - Marcar notificación como leída
            put("/{id}/read") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    val notificationId = call.parameters["id"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("ID de notificación inválido")
                    
                    notificationService.markAsRead(notificationId, userId)
                    call.respond(HttpStatusCode.OK, successResponse<String>("Notificación marcada como leída", null))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, errorResponse(e.message ?: "Solicitud inválida"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
            
            // PUT /api/notifications/read-all - Marcar todas las notificaciones como leídas
            put("/read-all") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    notificationService.markAllAsRead(userId)
                    call.respond(HttpStatusCode.OK, successResponse<String>("Todas las notificaciones marcadas como leídas", null))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.Unauthorized, errorResponse(e.message ?: "No autorizado"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
            
            // DELETE /api/notifications/{id} - Eliminar notificación
            delete("/{id}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    val notificationId = call.parameters["id"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("ID de notificación inválido")
                    
                    notificationService.deleteNotification(notificationId, userId)
                    call.respond(HttpStatusCode.OK, successResponse<String>("Notificación eliminada", null))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, errorResponse(e.message ?: "Solicitud inválida"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
        }
    }
}
