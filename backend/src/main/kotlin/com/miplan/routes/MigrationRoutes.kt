package com.miplan.routes

import com.miplan.database.Migrations
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Rutas para ejecutar migraciones manualmente (temporal)
 */
fun Route.migrationRoutes() {
    route("/api/migrate") {
        get {
            try {
                println("🔧 Ejecutando migraciones manualmente...")
                Migrations.runMigrations()
                call.respond(HttpStatusCode.OK, mapOf(
                    "success" to true,
                    "message" to "Migraciones ejecutadas correctamente"
                ))
            } catch (e: Exception) {
                println("❌ Error ejecutando migraciones: ${e.message}")
                e.printStackTrace()
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "success" to false,
                    "message" to "Error: ${e.message}"
                ))
            }
        }
    }
}
