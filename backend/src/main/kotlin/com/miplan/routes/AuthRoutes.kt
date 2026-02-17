package com.miplan.routes

import com.miplan.models.requests.LoginRequest
import com.miplan.models.requests.RegisterRequest
import com.miplan.models.responses.errorResponse
import com.miplan.models.responses.successResponse
import com.miplan.services.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Rutas de autenticación
 */
fun Route.authRoutes(authService: AuthService) {
    
    route("/api/auth") {
        
        // POST /api/auth/register
        post("/register") {
            try {
                val request = call.receive<RegisterRequest>()
                val message = authService.register(request.email, request.password, request.name)
                call.respond(HttpStatusCode.Created, successResponse<String>(message, null))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, errorResponse(e.message ?: "Error en el registro"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
            }
        }
        
        // POST /api/auth/login
        post("/login") {
            try {
                val request = call.receive<LoginRequest>()
                val authResponse = authService.login(request.email, request.password)
                call.respond(HttpStatusCode.OK, successResponse("Login exitoso", authResponse))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.Unauthorized, errorResponse(e.message ?: "Credenciales inválidas"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
            }
        }
        
        // GET /api/auth/verify/{token}
        get("/verify/{token}") {
            try {
                val token = call.parameters["token"] ?: throw IllegalArgumentException("Token no proporcionado")
                authService.verifyEmail(token)
                
                // Cargar y devolver página HTML de éxito
                val htmlContent = this::class.java.classLoader
                    .getResource("templates/email_verified.html")
                    ?.readText() ?: "<h1>Email verificado exitosamente</h1>"
                
                call.respondText(htmlContent, io.ktor.http.ContentType.Text.Html, HttpStatusCode.OK)
            } catch (e: IllegalArgumentException) {
                // Cargar y devolver página HTML de error
                val htmlContent = this::class.java.classLoader
                    .getResource("templates/email_error.html")
                    ?.readText()
                    ?.replace("{{ERROR_MESSAGE}}", e.message ?: "Token inválido")
                    ?: "<h1>Error: ${e.message}</h1>"
                
                call.respondText(htmlContent, io.ktor.http.ContentType.Text.Html, HttpStatusCode.BadRequest)
            } catch (e: Exception) {
                // Cargar y devolver página HTML de error
                val htmlContent = this::class.java.classLoader
                    .getResource("templates/email_error.html")
                    ?.readText()
                    ?.replace("{{ERROR_MESSAGE}}", "Error interno del servidor")
                    ?: "<h1>Error interno del servidor</h1>"
                
                call.respondText(htmlContent, io.ktor.http.ContentType.Text.Html, HttpStatusCode.InternalServerError)
            }
        }
        
        // Rutas protegidas
        authenticate("jwt") {
            
            // GET /api/auth/me
            get("/me") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                        ?: throw IllegalArgumentException("Usuario no autenticado")
                    
                    val user = authService.getCurrentUser(userId)
                    call.respond(HttpStatusCode.OK, successResponse("Usuario obtenido", user))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.Unauthorized, errorResponse(e.message ?: "No autorizado"))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, errorResponse("Error interno del servidor"))
                }
            }
            
            // POST /api/auth/logout
            post("/logout") {
                call.respond(HttpStatusCode.OK, successResponse<String>("Sesión cerrada exitosamente", null))
            }
        }
    }
}
