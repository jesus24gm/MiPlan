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
                call.respondText(
                    "✅ Migraciones ejecutadas correctamente. La columna image_url ha sido agregada a la tabla tasks.",
                    ContentType.Text.Plain,
                    HttpStatusCode.OK
                )
            } catch (e: Exception) {
                println("❌ Error ejecutando migraciones: ${e.message}")
                e.printStackTrace()
                call.respondText(
                    "❌ Error ejecutando migraciones: ${e.message}",
                    ContentType.Text.Plain,
                    HttpStatusCode.InternalServerError
                )
            }
        }
    }
}
