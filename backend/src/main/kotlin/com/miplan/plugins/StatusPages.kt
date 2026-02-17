package com.miplan.plugins

import com.miplan.models.responses.errorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

/**
 * Configuración de manejo de errores
 */
fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when (cause) {
                is IllegalArgumentException -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        errorResponse(cause.message ?: "Solicitud inválida")
                    )
                }
                else -> {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        errorResponse("Error interno del servidor")
                    )
                }
            }
            call.application.environment.log.error("Error no manejado", cause)
        }
    }
}
