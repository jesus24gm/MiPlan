package com.miplan.routes

import com.miplan.models.requests.ChangePasswordRequest
import com.miplan.models.requests.UpdateProfileRequest
import com.miplan.models.responses.errorResponse
import com.miplan.models.responses.successResponse
import com.miplan.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Rutas de gestión de usuarios
 */
fun Route.userRoutes(userService: UserService) {
    
    route("/api/users") {
        
        // Rutas protegidas - requieren autenticación
        authenticate("jwt") {
            
            // PUT /api/users/profile - Actualizar perfil
            put("/profile") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    val request = call.receive<UpdateProfileRequest>()
                    val user = userService.updateProfile(userId, request.name, request.email)
                    call.respond(HttpStatusCode.OK, successResponse("Perfil actualizado exitosamente", user))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, errorResponse(e.message ?: "Error al actualizar perfil"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
            
            // PUT /api/users/password - Cambiar contraseña
            put("/password") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    val request = call.receive<ChangePasswordRequest>()
                    userService.changePassword(userId, request.currentPassword, request.newPassword)
                    call.respond(HttpStatusCode.OK, successResponse<String>("Contraseña actualizada exitosamente", null))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, errorResponse(e.message ?: "Error al cambiar contraseña"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
            
            // DELETE /api/users/account - Eliminar cuenta
            delete("/account") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    userService.deleteAccount(userId)
                    call.respond(HttpStatusCode.OK, successResponse<String>("Cuenta eliminada exitosamente", null))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, errorResponse(e.message ?: "Error al eliminar cuenta"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
            
            // GET /api/users/stats - Obtener estadísticas del usuario
            get("/stats") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    val stats = userService.getUserStats(userId)
                    call.respond(HttpStatusCode.OK, successResponse("Estadísticas obtenidas", stats))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, errorResponse(e.message ?: "Error al obtener estadísticas"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
            
            // GET /api/users/{id} - Obtener usuario por ID
            get("/{id}") {
                try {
                    val userId = call.parameters["id"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("ID de usuario inválido")
                    
                    val user = userService.getUserById(userId)
                    call.respond(HttpStatusCode.OK, successResponse("Usuario obtenido", user))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.NotFound, errorResponse(e.message ?: "Usuario no encontrado"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
        }
    }
}
