package com.miplan.repositories

import com.miplan.database.CardChecklists
import com.miplan.database.ChecklistItems
import com.miplan.models.CardChecklist
import com.miplan.models.ChecklistItem
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

/**
 * Repositorio para operaciones CRUD de checklists y sus items
 */
class ChecklistRepository {
    
    // ==================== CHECKLISTS ====================
    
    /**
     * Crear un nuevo checklist
     */
    fun createChecklist(cardId: Int, title: String): CardChecklist {
        return transaction {
            val id = CardChecklists.insertAndGetId {
                it[CardChecklists.cardId] = cardId
                it[CardChecklists.title] = title
                it[createdAt] = LocalDateTime.now()
            }
            
            findChecklistById(id.value)!!
        }
    }
    
    /**
     * Buscar checklist por ID
     */
    fun findChecklistById(id: Int): CardChecklist? {
        return transaction {
            CardChecklists.select { CardChecklists.id eq id }
                .mapNotNull { rowToChecklist(it) }
                .singleOrNull()
        }
    }
    
    /**
     * Obtener todos los checklists de una tarjeta
     */
    fun findChecklistsByCardId(cardId: Int): List<CardChecklist> {
        return transaction {
            CardChecklists.select { CardChecklists.cardId eq cardId }
                .map { rowToChecklist(it) }
        }
    }
    
    /**
     * Actualizar checklist
     */
    fun updateChecklist(id: Int, title: String): CardChecklist? {
        return transaction {
            val updateCount = CardChecklists.update({ CardChecklists.id eq id }) {
                it[CardChecklists.title] = title
            }
            
            if (updateCount > 0) findChecklistById(id) else null
        }
    }
    
    /**
     * Eliminar checklist
     */
    fun deleteChecklist(id: Int): Boolean {
        return transaction {
            CardChecklists.deleteWhere { CardChecklists.id eq id } > 0
        }
    }
    
    // ==================== CHECKLIST ITEMS ====================
    
    /**
     * Crear un nuevo item de checklist
     */
    fun createItem(checklistId: Int, title: String, position: Int?): ChecklistItem {
        return transaction {
            // Si no se especifica posición, obtener la siguiente
            val nextPosition = position ?: run {
                ChecklistItems.select { ChecklistItems.checklistId eq checklistId }
                    .count()
                    .toInt()
            }
            
            val id = ChecklistItems.insertAndGetId {
                it[ChecklistItems.checklistId] = checklistId
                it[ChecklistItems.title] = title
                it[isCompleted] = false
                it[ChecklistItems.position] = nextPosition
                it[createdAt] = LocalDateTime.now()
            }
            
            findItemById(id.value)!!
        }
    }
    
    /**
     * Buscar item por ID
     */
    fun findItemById(id: Int): ChecklistItem? {
        return transaction {
            ChecklistItems.select { ChecklistItems.id eq id }
                .mapNotNull { rowToItem(it) }
                .singleOrNull()
        }
    }
    
    /**
     * Obtener todos los items de un checklist
     */
    fun findItemsByChecklistId(checklistId: Int): List<ChecklistItem> {
        return transaction {
            ChecklistItems.select { ChecklistItems.checklistId eq checklistId }
                .orderBy(ChecklistItems.position to SortOrder.ASC)
                .map { rowToItem(it) }
        }
    }
    
    /**
     * Actualizar item
     */
    fun updateItem(
        id: Int,
        title: String?,
        isCompleted: Boolean?,
        position: Int?
    ): ChecklistItem? {
        return transaction {
            val updateCount = ChecklistItems.update({ ChecklistItems.id eq id }) {
                title?.let { value -> it[ChecklistItems.title] = value }
                isCompleted?.let { value -> it[ChecklistItems.isCompleted] = value }
                position?.let { value -> it[ChecklistItems.position] = value }
            }
            
            if (updateCount > 0) findItemById(id) else null
        }
    }
    
    /**
     * Eliminar item
     */
    fun deleteItem(id: Int): Boolean {
        return transaction {
            ChecklistItems.deleteWhere { ChecklistItems.id eq id } > 0
        }
    }
    
    /**
     * Calcular progreso de un checklist (porcentaje completado)
     */
    fun calculateProgress(checklistId: Int): Int {
        return transaction {
            val items = findItemsByChecklistId(checklistId)
            if (items.isEmpty()) return@transaction 0
            
            val completedCount = items.count { it.isCompleted }
            ((completedCount.toDouble() / items.size) * 100).toInt()
        }
    }
    
    // ==================== MAPPERS ====================
    
    private fun rowToChecklist(row: ResultRow): CardChecklist {
        return CardChecklist(
            id = row[CardChecklists.id].value,
            cardId = row[CardChecklists.cardId],
            title = row[CardChecklists.title],
            createdAt = row[CardChecklists.createdAt]
        )
    }
    
    private fun rowToItem(row: ResultRow): ChecklistItem {
        return ChecklistItem(
            id = row[ChecklistItems.id].value,
            checklistId = row[ChecklistItems.checklistId],
            title = row[ChecklistItems.title],
            isCompleted = row[ChecklistItems.isCompleted],
            position = row[ChecklistItems.position],
            createdAt = row[ChecklistItems.createdAt]
        )
    }
}
