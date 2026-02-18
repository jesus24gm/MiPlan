package com.miplan.services

import com.miplan.models.CardChecklist
import com.miplan.models.ChecklistItem
import com.miplan.models.requests.*
import com.miplan.models.responses.*
import com.miplan.repositories.ChecklistRepository
import java.time.format.DateTimeFormatter

/**
 * Servicio para lógica de negocio de checklists
 */
class ChecklistService(
    private val checklistRepository: ChecklistRepository
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    // ==================== CHECKLISTS ====================
    
    /**
     * Crear un nuevo checklist
     */
    fun createChecklist(request: CreateChecklistRequest): ChecklistResponse {
        val checklist = checklistRepository.createChecklist(
            cardId = request.cardId,
            title = request.title
        )
        
        return checklistToResponse(checklist)
    }
    
    /**
     * Obtener checklist por ID
     */
    fun getChecklistById(id: Int): ChecklistResponse? {
        val checklist = checklistRepository.findChecklistById(id) ?: return null
        return checklistToResponse(checklist)
    }
    
    /**
     * Obtener checklist con items y progreso
     */
    fun getChecklistWithItems(id: Int): ChecklistWithItemsResponse? {
        val checklist = checklistRepository.findChecklistById(id) ?: return null
        val items = checklistRepository.findItemsByChecklistId(id)
        val progress = checklistRepository.calculateProgress(id)
        
        return ChecklistWithItemsResponse(
            id = checklist.id,
            cardId = checklist.cardId,
            title = checklist.title,
            items = items.map { itemToResponse(it) },
            progress = progress,
            createdAt = checklist.createdAt.format(dateFormatter)
        )
    }
    
    /**
     * Obtener todos los checklists de una tarjeta
     */
    fun getChecklistsByCardId(cardId: Int): List<ChecklistResponse> {
        val checklists = checklistRepository.findChecklistsByCardId(cardId)
        return checklists.map { checklistToResponse(it) }
    }
    
    /**
     * Actualizar checklist
     */
    fun updateChecklist(id: Int, request: UpdateChecklistRequest): ChecklistResponse? {
        val checklist = checklistRepository.updateChecklist(
            id = id,
            title = request.title
        ) ?: return null
        
        return checklistToResponse(checklist)
    }
    
    /**
     * Eliminar checklist
     */
    fun deleteChecklist(id: Int): Boolean {
        return checklistRepository.deleteChecklist(id)
    }
    
    // ==================== CHECKLIST ITEMS ====================
    
    /**
     * Crear un nuevo item
     */
    fun createItem(request: CreateChecklistItemRequest): ChecklistItemResponse {
        val item = checklistRepository.createItem(
            checklistId = request.checklistId,
            title = request.title,
            position = request.position
        )
        
        return itemToResponse(item)
    }
    
    /**
     * Obtener item por ID
     */
    fun getItemById(id: Int): ChecklistItemResponse? {
        val item = checklistRepository.findItemById(id) ?: return null
        return itemToResponse(item)
    }
    
    /**
     * Obtener todos los items de un checklist
     */
    fun getItemsByChecklistId(checklistId: Int): List<ChecklistItemResponse> {
        val items = checklistRepository.findItemsByChecklistId(checklistId)
        return items.map { itemToResponse(it) }
    }
    
    /**
     * Actualizar item
     */
    fun updateItem(id: Int, request: UpdateChecklistItemRequest): ChecklistItemResponse? {
        val item = checklistRepository.updateItem(
            id = id,
            title = request.title,
            isCompleted = request.isCompleted,
            position = request.position
        ) ?: return null
        
        return itemToResponse(item)
    }
    
    /**
     * Toggle completado de un item
     */
    fun toggleItemCompleted(id: Int): ChecklistItemResponse? {
        val item = checklistRepository.findItemById(id) ?: return null
        
        val updatedItem = checklistRepository.updateItem(
            id = id,
            title = null,
            isCompleted = !item.isCompleted,
            position = null
        ) ?: return null
        
        return itemToResponse(updatedItem)
    }
    
    /**
     * Eliminar item
     */
    fun deleteItem(id: Int): Boolean {
        return checklistRepository.deleteItem(id)
    }
    
    // ==================== MAPPERS ====================
    
    private fun checklistToResponse(checklist: CardChecklist): ChecklistResponse {
        return ChecklistResponse(
            id = checklist.id,
            cardId = checklist.cardId,
            title = checklist.title,
            createdAt = checklist.createdAt.format(dateFormatter)
        )
    }
    
    private fun itemToResponse(item: ChecklistItem): ChecklistItemResponse {
        return ChecklistItemResponse(
            id = item.id,
            checklistId = item.checklistId,
            title = item.title,
            isCompleted = item.isCompleted,
            position = item.position,
            createdAt = item.createdAt.format(dateFormatter)
        )
    }
}
