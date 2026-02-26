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
        
        // Migración 0: Limpiar tablas Kanban existentes (si existen)
        migration0_CleanupKanbanTables()
        
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
        
        // Migración 8: Agregar due_date a cards
        migration8_AddDueDateToCards()
        
        // Migración 9: Agregar avatar_url a users
        migration9_AddAvatarUrlToUsers()
        
        // Migración 10: Crear tabla task_collaborators
        migration10_CreateTaskCollaboratorsTable()
        
        println("✅ Proceso de migraciones completado")
    }
    
    private fun migration0_CleanupKanbanTables() {
        try {
            transaction {
                // Eliminar en orden inverso de dependencias
                exec("DROP TABLE IF EXISTS card_attachments")
                exec("DROP TABLE IF EXISTS checklist_items")
                exec("DROP TABLE IF EXISTS card_checklists")
                exec("DROP TABLE IF EXISTS cards")
                exec("DROP TABLE IF EXISTS columns")
            }
        } catch (e: Exception) {
            // Silenciar error
        }
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
                exec("""
                    CREATE TABLE columns (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        board_id INT NOT NULL,
                        title VARCHAR(255) NOT NULL,
                        position INT DEFAULT 0,
                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP NOT NULL,
                        FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE
                    )
                """.trimIndent())
            }
        } catch (e: Exception) {
            // Silenciar error
        }
    }
    
    private fun migration4_CreateCardsTable() {
        try {
            transaction {
                exec("""
                    CREATE TABLE cards (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        column_id INT NOT NULL,
                        title VARCHAR(255) NOT NULL,
                        description TEXT,
                        cover_image_url VARCHAR(500),
                        position INT DEFAULT 0,
                        task_id INT,
                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP NOT NULL,
                        FOREIGN KEY (column_id) REFERENCES columns(id) ON DELETE CASCADE,
                        FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE SET NULL
                    )
                """.trimIndent())
            }
        } catch (e: Exception) {
            // Silenciar error
        }
    }
    
    private fun migration5_CreateCardChecklistsTable() {
        try {
            transaction {
                exec("""
                    CREATE TABLE card_checklists (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        card_id INT NOT NULL,
                        title VARCHAR(255) NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE
                    )
                """.trimIndent())
            }
        } catch (e: Exception) {
            // Silenciar error
        }
    }
    
    private fun migration6_CreateChecklistItemsTable() {
        try {
            transaction {
                exec("""
                    CREATE TABLE checklist_items (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        checklist_id INT NOT NULL,
                        title VARCHAR(255) NOT NULL,
                        is_completed BOOLEAN DEFAULT FALSE,
                        position INT DEFAULT 0,
                        created_at TIMESTAMP NOT NULL,
                        FOREIGN KEY (checklist_id) REFERENCES card_checklists(id) ON DELETE CASCADE
                    )
                """.trimIndent())
            }
        } catch (e: Exception) {
            // Silenciar error
        }
    }
    
    private fun migration7_CreateCardAttachmentsTable() {
        try {
            transaction {
                exec("""
                    CREATE TABLE card_attachments (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        card_id INT NOT NULL,
                        file_url VARCHAR(500) NOT NULL,
                        file_name VARCHAR(255) NOT NULL,
                        file_type VARCHAR(50) NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE
                    )
                """.trimIndent())
            }
        } catch (e: Exception) {
            // Silenciar error
        }
    }
    
    private fun migration8_AddDueDateToCards() {
        try {
            transaction {
                val checkQuery = """
                    SELECT column_name 
                    FROM information_schema.columns 
                    WHERE table_name = 'cards' 
                    AND column_name = 'due_date'
                """.trimIndent()
                
                val exists = exec(checkQuery) { rs -> rs.next() } ?: false
                
                if (!exists) {
                    println("📝 Migración 8: Agregando columna due_date a cards...")
                    exec("ALTER TABLE cards ADD COLUMN due_date TIMESTAMP NULL")
                    println("✅ Migración 8: Completada")
                } else {
                    println("ℹ️ Migración 8: Ya aplicada")
                }
            }
        } catch (e: Exception) {
            println("❌ Migración 8: Error - ${e.message}")
        }
    }
    
    private fun migration9_AddAvatarUrlToUsers() {
        try {
            transaction {
                val checkQuery = """
                    SELECT column_name 
                    FROM information_schema.columns 
                    WHERE table_name = 'users' 
                    AND column_name = 'avatar_url'
                """.trimIndent()
                
                val exists = exec(checkQuery) { rs -> rs.next() } ?: false
                
                if (!exists) {
                    println("📝 Migración 9: Agregando columna avatar_url a users...")
                    exec("ALTER TABLE users ADD COLUMN avatar_url VARCHAR(500)")
                    println("✅ Migración 9: Completada")
                } else {
                    println("ℹ️ Migración 9: Ya aplicada")
                }
            }
        } catch (e: Exception) {
            println("❌ Migración 9: Error - ${e.message}")
        }
    }
    
    private fun migration10_CreateTaskCollaboratorsTable() {
        try {
            transaction {
                val checkQuery = """
                    SELECT table_name 
                    FROM information_schema.tables 
                    WHERE table_name = 'task_collaborators'
                """.trimIndent()
                
                val exists = exec(checkQuery) { rs -> rs.next() } ?: false
                
                if (!exists) {
                    println("📝 Migración 10: Creando tabla task_collaborators...")
                    exec("""
                        CREATE TABLE task_collaborators (
                            task_id INTEGER NOT NULL,
                            user_id INTEGER NOT NULL,
                            role VARCHAR(50) NOT NULL DEFAULT 'VIEWER',
                            added_at TIMESTAMP NOT NULL,
                            added_by INTEGER NOT NULL,
                            PRIMARY KEY (task_id, user_id),
                            FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (added_by) REFERENCES users(id)
                        )
                    """.trimIndent())
                    
                    // Crear índices para mejorar rendimiento
                    exec("CREATE INDEX idx_task_collaborators_task_id ON task_collaborators(task_id)")
                    exec("CREATE INDEX idx_task_collaborators_user_id ON task_collaborators(user_id)")
                    
                    println("✅ Migración 10: Completada")
                } else {
                    println("ℹ️ Migración 10: Ya aplicada")
                }
            }
        } catch (e: Exception) {
            println("❌ Migración 10: Error - ${e.message}")
        }
    }
}
