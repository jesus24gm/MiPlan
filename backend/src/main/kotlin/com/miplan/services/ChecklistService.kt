package com.miplan.services

import com.miplan.models.CardChecklist
import com.miplan.models.ChecklistItem
import com.miplan.models.requests.*
import com.miplan.models.responses.*
import com.miplan.repositories.ChecklistRepository
import java.time.format.DateTimeFormatter

class ChecklistService(
    private val checklistRepository: ChecklistRepository
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    suspend fun createChecklist(request: CreateChecklistRequest): ChecklistResponse? {
        val checklist = checklistRepository.createChecklist(
            cardId = request.cardId,
            title = request.title
        ) ?: return null
        
        return checklistToResponse(checklist)
    }
    
    suspend fun getChecklistById(id: Int): ChecklistResponse? {
        val checklist = checklistRepository.findChecklistById(id) ?: return null
        return checklistToResponse(checklist)
    }
    
    suspend fun getChecklistWithItems(id: Int): ChecklistWithItemsResponse? {
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
    
    suspend fun getChecklistsByCardId(cardId: Int): List<ChecklistResponse> {
        val checklists = checklistRepository.findChecklistsByCardId(cardId)
        return checklists.map { checklistToResponse(it) }
    }
    
    suspend fun updateChecklist(id: Int, request: UpdateChecklistRequest): ChecklistResponse? {
        val updated = checklistRepository.updateChecklist(id, request.title)
        if (!updated) return null
        
        return checklistRepository.findChecklistById(id)?.let { checklistToResponse(it) }
    }
    
    suspend fun deleteChecklist(id: Int): Boolean {
        return checklistRepository.deleteChecklist(id)
    }
    
    suspend fun createItem(request: CreateChecklistItemRequest): ChecklistItemResponse? {
        val item = checklistRepository.createItem(
            checklistId = request.checklistId,
            title = request.title,
            position = request.position
        ) ?: return null
        
        return itemToResponse(item)
    }
    
    suspend fun getItemById(id: Int): ChecklistItemResponse? {
        val item = checklistRepository.findItemById(id) ?: return null
        return itemToResponse(item)
    }
    
    suspend fun getItemsByChecklistId(checklistId: Int): List<ChecklistItemResponse> {
        val items = checklistRepository.findItemsByChecklistId(checklistId)
        return items.map { itemToResponse(it) }
    }
    
    suspend fun updateItem(id: Int, request: UpdateChecklistItemRequest): ChecklistItemResponse? {
        val updated = checklistRepository.updateItem(
            id = id,
            title = request.title,
            isCompleted = request.isCompleted,
            position = request.position
        )
        
        if (!updated) return null
        
        return checklistRepository.findItemById(id)?.let { itemToResponse(it) }
    }
    
    suspend fun toggleItemCompleted(id: Int): ChecklistItemResponse? {
        val item = checklistRepository.findItemById(id) ?: return null
        
        val updated = checklistRepository.updateItem(
            id = id,
            title = null,
            isCompleted = !item.isCompleted,
            position = null
        )
        
        if (!updated) return null
        
        return checklistRepository.findItemById(id)?.let { itemToResponse(it) }
    }
    
    suspend fun deleteItem(id: Int): Boolean {
        return checklistRepository.deleteItem(id)
    }
    
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
