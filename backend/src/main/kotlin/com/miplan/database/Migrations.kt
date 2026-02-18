package com.miplan.database

import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Migraciones de base de datos
 */
object Migrations {
    
    /**
     * Ejecuta todas las migraciones pendientes
     */
    fun runMigrations() {
        println("🔄 Ejecutando migraciones de base de datos...")
        
        transaction {
            // Migración 1: Agregar columna image_url
            try {
                exec("ALTER TABLE tasks ADD COLUMN IF NOT EXISTS image_url VARCHAR(500)")
                println("✅ Migración 1: Columna image_url agregada")
            } catch (e: Exception) {
                println("⚠️ Migración 1: ${e.message}")
            }
        }
        
        println("✅ Migraciones completadas")
    }
}
