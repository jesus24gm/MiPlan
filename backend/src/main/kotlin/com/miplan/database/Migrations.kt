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
        
        // Migración 1: Agregar image_url a tasks
        migration1_AddImageUrlToTasks()
        
        // Migración 2: Agregar background_image_url a boards
        migration2_AddBackgroundImageUrlToBoards()
        
        // Migración 3: Crear tabla columns
        migration3_CreateColumnsTable()
        
        // Migración 4: Crear tabla cards
        migration4_CreateCardsTable()
        
        // Migración 5: Crear tabla card_checklists
        migration5_CreateCardChecklistsTable()
        
        // Migración 6: Crear tabla checklist_items
        migration6_CreateChecklistItemsTable()
        
        // Migración 7: Crear tabla card_attachments
        migration7_CreateCardAttachmentsTable()
        
        println("✅ Proceso de migraciones completado")
    }
    
    private fun migration1_AddImageUrlToTasks() {
        try {
            transaction {
                val checkQuery = """
                    SELECT column_name 
                    FROM information_schema.columns 
                    WHERE table_name = 'tasks' 
                    AND column_name = 'image_url'
                """.trimIndent()
                
                val exists = exec(checkQuery) { rs -> rs.next() } ?: false
                
                if (!exists) {
                    println("📝 Migración 1: Agregando columna image_url a tasks...")
                    exec("ALTER TABLE tasks ADD COLUMN image_url VARCHAR(500)")
                    println("✅ Migración 1: Completada")
                } else {
                    println("ℹ️ Migración 1: Ya aplicada")
                }
            }
        } catch (e: Exception) {
            println("❌ Migración 1: Error - ${e.message}")
        }
    }
    
    private fun migration2_AddBackgroundImageUrlToBoards() {
        try {
            transaction {
                val checkQuery = """
                    SELECT column_name 
                    FROM information_schema.columns 
                    WHERE table_name = 'boards' 
                    AND column_name = 'background_image_url'
                """.trimIndent()
                
                val exists = exec(checkQuery) { rs -> rs.next() } ?: false
                
                if (!exists) {
                    println("📝 Migración 2: Agregando columna background_image_url a boards...")
                    exec("ALTER TABLE boards ADD COLUMN background_image_url VARCHAR(500)")
                    println("✅ Migración 2: Completada")
                } else {
                    println("ℹ️ Migración 2: Ya aplicada")
                }
            }
        } catch (e: Exception) {
            println("❌ Migración 2: Error - ${e.message}")
        }
    }
    
    private fun migration3_CreateColumnsTable() {
        try {
            transaction {
                val checkQuery = """
                    SELECT table_name 
                    FROM information_schema.tables 
                    WHERE table_name = 'columns'
                """.trimIndent()
                
                val exists = exec(checkQuery) { rs -> rs.next() } ?: false
                
                if (!exists) {
                    println("📝 Migración 3: Creando tabla columns...")
                    exec("""
                        CREATE TABLE columns (
                            id SERIAL PRIMARY KEY,
                            board_id INTEGER NOT NULL REFERENCES boards(id) ON DELETE CASCADE,
                            title VARCHAR(255) NOT NULL,
                            position INTEGER DEFAULT 0,
                            created_at TIMESTAMP NOT NULL,
                            updated_at TIMESTAMP NOT NULL
                        )
                    """.trimIndent())
                    println("✅ Migración 3: Completada")
                } else {
                    println("ℹ️ Migración 3: Ya aplicada")
                }
            }
        } catch (e: Exception) {
            println("❌ Migración 3: Error - ${e.message}")
        }
    }
    
    private fun migration4_CreateCardsTable() {
        try {
            transaction {
                val checkQuery = """
                    SELECT table_name 
                    FROM information_schema.tables 
                    WHERE table_name = 'cards'
                """.trimIndent()
                
                val exists = exec(checkQuery) { rs -> rs.next() } ?: false
                
                if (!exists) {
                    println("📝 Migración 4: Creando tabla cards...")
                    exec("""
                        CREATE TABLE cards (
                            id SERIAL PRIMARY KEY,
                            column_id INTEGER NOT NULL REFERENCES columns(id) ON DELETE CASCADE,
                            title VARCHAR(255) NOT NULL,
                            description TEXT,
                            cover_image_url VARCHAR(500),
                            position INTEGER DEFAULT 0,
                            task_id INTEGER REFERENCES tasks(id) ON DELETE SET NULL,
                            created_at TIMESTAMP NOT NULL,
                            updated_at TIMESTAMP NOT NULL
                        )
                    """.trimIndent())
                    println("✅ Migración 4: Completada")
                } else {
                    println("ℹ️ Migración 4: Ya aplicada")
                }
            }
        } catch (e: Exception) {
            println("❌ Migración 4: Error - ${e.message}")
        }
    }
    
    private fun migration5_CreateCardChecklistsTable() {
        try {
            transaction {
                val checkQuery = """
                    SELECT table_name 
                    FROM information_schema.tables 
                    WHERE table_name = 'card_checklists'
                """.trimIndent()
                
                val exists = exec(checkQuery) { rs -> rs.next() } ?: false
                
                if (!exists) {
                    println("📝 Migración 5: Creando tabla card_checklists...")
                    exec("""
                        CREATE TABLE card_checklists (
                            id SERIAL PRIMARY KEY,
                            card_id INTEGER NOT NULL REFERENCES cards(id) ON DELETE CASCADE,
                            title VARCHAR(255) NOT NULL,
                            created_at TIMESTAMP NOT NULL
                        )
                    """.trimIndent())
                    println("✅ Migración 5: Completada")
                } else {
                    println("ℹ️ Migración 5: Ya aplicada")
                }
            }
        } catch (e: Exception) {
            println("❌ Migración 5: Error - ${e.message}")
        }
    }
    
    private fun migration6_CreateChecklistItemsTable() {
        try {
            transaction {
                val checkQuery = """
                    SELECT table_name 
                    FROM information_schema.tables 
                    WHERE table_name = 'checklist_items'
                """.trimIndent()
                
                val exists = exec(checkQuery) { rs -> rs.next() } ?: false
                
                if (!exists) {
                    println("📝 Migración 6: Creando tabla checklist_items...")
                    exec("""
                        CREATE TABLE checklist_items (
                            id SERIAL PRIMARY KEY,
                            checklist_id INTEGER NOT NULL REFERENCES card_checklists(id) ON DELETE CASCADE,
                            title VARCHAR(255) NOT NULL,
                            is_completed BOOLEAN DEFAULT FALSE,
                            position INTEGER DEFAULT 0,
                            created_at TIMESTAMP NOT NULL
                        )
                    """.trimIndent())
                    println("✅ Migración 6: Completada")
                } else {
                    println("ℹ️ Migración 6: Ya aplicada")
                }
            }
        } catch (e: Exception) {
            println("❌ Migración 6: Error - ${e.message}")
        }
    }
    
    private fun migration7_CreateCardAttachmentsTable() {
        try {
            transaction {
                val checkQuery = """
                    SELECT table_name 
                    FROM information_schema.tables 
                    WHERE table_name = 'card_attachments'
                """.trimIndent()
                
                val exists = exec(checkQuery) { rs -> rs.next() } ?: false
                
                if (!exists) {
                    println("📝 Migración 7: Creando tabla card_attachments...")
                    exec("""
                        CREATE TABLE card_attachments (
                            id SERIAL PRIMARY KEY,
                            card_id INTEGER NOT NULL REFERENCES cards(id) ON DELETE CASCADE,
                            file_url VARCHAR(500) NOT NULL,
                            file_name VARCHAR(255) NOT NULL,
                            file_type VARCHAR(50) NOT NULL,
                            created_at TIMESTAMP NOT NULL
                        )
                    """.trimIndent())
                    println("✅ Migración 7: Completada")
                } else {
                    println("ℹ️ Migración 7: Ya aplicada")
                }
            }
        } catch (e: Exception) {
            println("❌ Migración 7: Error - ${e.message}")
        }
    }
}
