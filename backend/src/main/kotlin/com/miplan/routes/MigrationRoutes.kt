package com.miplan.routes

import com.miplan.database.Migrations
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Statement

/**
 * Rutas para ejecutar migraciones manualmente (temporal)
 */
fun Route.migrationRoutes() {
    route("/api/migrate") {
        get {
            try {
                println("🔧 Ejecutando migraciones manualmente...")
                
                val result = transaction {
                    // Método directo con SQL puro
                    val stmt = this.connection.createStatement()
                    try {
                        // Intentar agregar la columna
                        stmt.execute("ALTER TABLE tasks ADD COLUMN IF NOT EXISTS image_url VARCHAR(500)")
                        "✅ Columna image_url agregada o ya existía"
                    } catch (e: Exception) {
                        "⚠️ Error: ${e.message}"
                    } finally {
                        stmt.close()
                    }
                }
                
                println(result)
                call.respondText(
                    "✅ Migración ejecutada.\n$result\n\nLa columna image_url debería estar disponible ahora.",
                    ContentType.Text.Plain,
                    HttpStatusCode.OK
                )
            } catch (e: Exception) {
                println("❌ Error ejecutando migraciones: ${e.message}")
                e.printStackTrace()
                call.respondText(
                    "❌ Error ejecutando migraciones: ${e.message}\n\nStack trace: ${e.stackTraceToString()}",
                    ContentType.Text.Plain,
                    HttpStatusCode.InternalServerError
                )
            }
        }
    }
}
