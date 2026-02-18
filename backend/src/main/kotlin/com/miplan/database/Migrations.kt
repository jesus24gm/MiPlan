package com.miplan.database

import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

/**
 * Migraciones de base de datos
 */
object Migrations {
    
    /**
     * Ejecuta todas las migraciones pendientes
     */
    fun runMigrations() {
        println("🔄 Ejecutando migraciones de base de datos...")
        
        try {
            transaction {
                // Verificar si la columna existe
                val checkQuery = """
                    SELECT column_name 
                    FROM information_schema.columns 
                    WHERE table_name = 'tasks' 
                    AND column_name = 'image_url'
                """.trimIndent()
                
                val exists = exec(checkQuery) { rs ->
                    rs.next()
                } ?: false
                
                if (!exists) {
                    println("📝 Columna image_url no existe, agregando...")
                    exec("ALTER TABLE tasks ADD COLUMN image_url VARCHAR(500)")
                    println("✅ Migración 1: Columna image_url agregada exitosamente")
                } else {
                    println("ℹ️ Columna image_url ya existe, omitiendo migración")
                }
            }
        } catch (e: Exception) {
            println("❌ Error en migraciones: ${e.message}")
            e.printStackTrace()
            
            // Intentar método alternativo
            try {
                println("🔄 Intentando método alternativo...")
                transaction {
                    exec("ALTER TABLE tasks ADD COLUMN image_url VARCHAR(500)")
                    println("✅ Columna agregada con método alternativo")
                }
            } catch (e2: Exception) {
                println("❌ Método alternativo falló: ${e2.message}")
                println("⚠️ IMPORTANTE: Ejecutar manualmente: ALTER TABLE tasks ADD COLUMN image_url VARCHAR(500);")
            }
        }
        
        println("✅ Proceso de migraciones completado")
    }
}
