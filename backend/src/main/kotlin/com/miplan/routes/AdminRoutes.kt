package com.miplan.routes

import com.miplan.models.requests.UpdateUserRoleRequest
import com.miplan.models.requests.ToggleUserStatusRequest
import com.miplan.models.responses.errorResponse
import com.miplan.models.responses.successResponse
import com.miplan.services.AdminService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Rutas de administración del sistema
 * Solo accesibles por usuarios con rol ADMIN
 */
fun Route.adminRoutes(adminService: AdminService) {
    
    route("/api/admin") {
        
        // Todas las rutas requieren autenticación
        authenticate("jwt") {
            
            // GET /api/admin/users - Obtener todos los usuarios
            get("/users") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userRole = principal?.payload?.getClaim("role")?.asString()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    // Verificar que sea administrador
                    if (userRole != "ADMIN") {
                        call.respond(
                            HttpStatusCode.Forbidden,
                            errorResponse("Acceso denegado. Se requieren permisos de administrador.")
                        )
                        return@get
                    }
                    
                    val users = adminService.getAllUsers()
                    call.respond(HttpStatusCode.OK, successResponse("Usuarios obtenidos", users))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, errorResponse(e.message ?: "Error al obtener usuarios"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
            
            // PATCH /api/admin/users/{userId}/role - Actualizar rol de usuario
            patch("/users/{userId}/role") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val currentUserId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    val userRole = principal.payload.getClaim("role")?.asString()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    // Verificar que sea administrador
                    if (userRole != "ADMIN") {
                        call.respond(
                            HttpStatusCode.Forbidden,
                            errorResponse("Acceso denegado. Se requieren permisos de administrador.")
                        )
                        return@patch
                    }
                    
                    val userId = call.parameters["userId"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("ID de usuario inválido")
                    
                    val request = call.receive<UpdateUserRoleRequest>()
                    
                    // No permitir que un admin se quite a sí mismo el rol de admin
                    if (userId == currentUserId && request.role == "USER") {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            errorResponse("No puedes quitarte el rol de administrador a ti mismo")
                        )
                        return@patch
                    }
                    
                    val user = adminService.updateUserRole(userId, request.role)
                    call.respond(HttpStatusCode.OK, successResponse("Rol actualizado exitosamente", user))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, errorResponse(e.message ?: "Error al actualizar rol"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
            
            // GET /api/admin/stats - Obtener estadísticas del sistema
            get("/stats") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userRole = principal?.payload?.getClaim("role")?.asString()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    // Verificar que sea administrador
                    if (userRole != "ADMIN") {
                        call.respond(
                            HttpStatusCode.Forbidden,
                            errorResponse("Acceso denegado. Se requieren permisos de administrador.")
                        )
                        return@get
                    }
                    
                    val stats = adminService.getAdminStats()
                    call.respond(HttpStatusCode.OK, successResponse("Estadísticas obtenidas", stats))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, errorResponse(e.message ?: "Error al obtener estadísticas"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
            
            // PATCH /api/admin/users/{userId}/status - Activar/suspender usuario
            patch("/users/{userId}/status") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val currentUserId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    val userRole = principal.payload.getClaim("role")?.asString()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    // Verificar que sea administrador
                    if (userRole != "ADMIN") {
                        call.respond(
                            HttpStatusCode.Forbidden,
                            errorResponse("Acceso denegado. Se requieren permisos de administrador.")
                        )
                        return@patch
                    }
                    
                    val userId = call.parameters["userId"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("ID de usuario inválido")
                    
                    val request = call.receive<ToggleUserStatusRequest>()
                    
                    // No permitir que un admin se suspenda a sí mismo
                    if (userId == currentUserId && !request.is_active) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            errorResponse("No puedes suspender tu propia cuenta")
                        )
                        return@patch
                    }
                    
                    val user = adminService.toggleUserStatus(userId, request.is_active)
                    call.respond(HttpStatusCode.OK, successResponse("Estado actualizado exitosamente", user))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, errorResponse(e.message ?: "Error al cambiar estado"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
            
            // DELETE /api/admin/users/:userId - Eliminar usuario
            delete("/{userId}") {
                try {
                    val currentUserId = call.principal<JWTPrincipal>()
                        ?.payload
                        ?.getClaim("userId")
                        ?.asInt() ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    val userRole = call.principal<JWTPrincipal>()
                        ?.payload
                        ?.getClaim("role")
                        ?.asString() ?: "USER"
                    
                    if (userRole != "ADMIN") {
                        call.respond(
                            HttpStatusCode.Forbidden,
                            errorResponse("Acceso denegado. Se requieren permisos de administrador.")
                        )
                        return@delete
                    }
                    
                    val userId = call.parameters["userId"]?.toIntOrNull()
                        ?: throw IllegalArgumentException("ID de usuario inválido")
                    
                    // No permitir que un admin se elimine a sí mismo
                    if (userId == currentUserId) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            errorResponse("No puedes eliminar tu propia cuenta")
                        )
                        return@delete
                    }
                    
                    val deleted = adminService.deleteUser(userId)
                    if (deleted) {
                        call.respond(HttpStatusCode.OK, successResponse("Usuario eliminado exitosamente", null))
                    } else {
                        call.respond(HttpStatusCode.NotFound, errorResponse("Usuario no encontrado"))
                    }
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, errorResponse(e.message ?: "Error al eliminar usuario"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
        }
    }
}
