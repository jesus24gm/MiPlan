package com.miplan.repositories

import com.miplan.database.CardChecklists
import com.miplan.database.ChecklistItems
import com.miplan.database.DatabaseFactory.dbQuery
import com.miplan.models.CardChecklist
import com.miplan.models.ChecklistItem
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

/**
 * Repositorio para operaciones CRUD de checklists y sus items
 */
class ChecklistRepository {
    
    // ==================== CHECKLISTS ====================
    
    /**
     * Crear un nuevo checklist
     */
    suspend fun createChecklist(cardId: Int, title: String): CardChecklist? = dbQuery {
        val now = LocalDateTime.now()
        val insertStatement = CardChecklists.insert {
            it[CardChecklists.cardId] = cardId
            it[CardChecklists.title] = title
            it[createdAt] = now
        }
        
        val id = insertStatement[CardChecklists.id]
        
        // Construir el objeto directamente en lugar de llamar a findChecklistById
        CardChecklist(
            id = id,
            cardId = cardId,
            title = title,
            createdAt = now
        )
    }
    
    /**
     * Buscar checklist por ID
     */
    suspend fun findChecklistById(id: Int): CardChecklist? = dbQuery {
        CardChecklists.select { CardChecklists.id eq id }
            .mapNotNull { rowToChecklist(it) }
            .singleOrNull()
    }
    
    /**
     * Obtener todos los checklists de una tarjeta
     */
    suspend fun findChecklistsByCardId(cardId: Int): List<CardChecklist> = dbQuery {
        CardChecklists.select { CardChecklists.cardId eq cardId }
            .map { rowToChecklist(it) }
    }
    
    /**
     * Actualizar checklist
     */
    suspend fun updateChecklist(id: Int, title: String): Boolean = dbQuery {
        CardChecklists.update({ CardChecklists.id eq id }) {
            it[CardChecklists.title] = title
        } > 0
    }
    
    /**
     * Eliminar checklist
     */
    suspend fun deleteChecklist(id: Int): Boolean = dbQuery {
        CardChecklists.deleteWhere { CardChecklists.id eq id } > 0
    }
    
    // ==================== CHECKLIST ITEMS ====================
    
    /**
     * Crear un nuevo item de checklist
     */
    suspend fun createItem(checklistId: Int, title: String, position: Int?): ChecklistItem? = dbQuery {
        // Si no se especifica posición, obtener la siguiente
        val nextPosition = position ?: ChecklistItems.select { ChecklistItems.checklistId eq checklistId }
            .count()
            .toInt()
        
        val now = LocalDateTime.now()
        val insertStatement = ChecklistItems.insert {
            it[ChecklistItems.checklistId] = checklistId
            it[ChecklistItems.title] = title
            it[isCompleted] = false
            it[ChecklistItems.position] = nextPosition
            it[createdAt] = now
        }
        
        val id = insertStatement[ChecklistItems.id]
        
        // Construir el objeto directamente en lugar de llamar a findItemById
        ChecklistItem(
            id = id,
            checklistId = checklistId,
            title = title,
            isCompleted = false,
            position = nextPosition,
            createdAt = now
        )
    }
    
    /**
     * Buscar item por ID
     */
    suspend fun findItemById(id: Int): ChecklistItem? = dbQuery {
        ChecklistItems.select { ChecklistItems.id eq id }
            .mapNotNull { rowToItem(it) }
            .singleOrNull()
    }
    
    /**
     * Obtener todos los items de un checklist
     */
    suspend fun findItemsByChecklistId(checklistId: Int): List<ChecklistItem> = dbQuery {
        ChecklistItems.select { ChecklistItems.checklistId eq checklistId }
            .orderBy(ChecklistItems.position to SortOrder.ASC)
            .map { rowToItem(it) }
    }
    
    /**
     * Actualizar item
     */
    suspend fun updateItem(
        id: Int,
        title: String?,
        isCompleted: Boolean?,
        position: Int?
    ): Boolean = dbQuery {
        ChecklistItems.update({ ChecklistItems.id eq id }) {
            title?.let { value -> it[ChecklistItems.title] = value }
            isCompleted?.let { value -> it[ChecklistItems.isCompleted] = value }
            position?.let { value -> it[ChecklistItems.position] = value }
        } > 0
    }
    
    /**
     * Eliminar item
     */
    suspend fun deleteItem(id: Int): Boolean = dbQuery {
        ChecklistItems.deleteWhere { ChecklistItems.id eq id } > 0
    }
    
    /**
     * Calcular progreso de un checklist (porcentaje completado)
     */
    suspend fun calculateProgress(checklistId: Int): Int = dbQuery {
        val items = findItemsByChecklistId(checklistId)
        if (items.isEmpty()) return@dbQuery 0
        
        val completedCount = items.count { it.isCompleted }
        ((completedCount.toDouble() / items.size) * 100).toInt()
    }
    
    // ==================== MAPPERS ====================
    
    private fun rowToChecklist(row: ResultRow): CardChecklist {
        return CardChecklist(
            id = row[CardChecklists.id],
            cardId = row[CardChecklists.cardId],
            title = row[CardChecklists.title],
            createdAt = row[CardChecklists.createdAt]
        )
    }
    
    private fun rowToItem(row: ResultRow): ChecklistItem {
        return ChecklistItem(
            id = row[ChecklistItems.id],
            checklistId = row[ChecklistItems.checklistId],
            title = row[ChecklistItems.title],
            isCompleted = row[ChecklistItems.isCompleted],
            position = row[ChecklistItems.position],
            createdAt = row[ChecklistItems.createdAt]
        )
    }
}
