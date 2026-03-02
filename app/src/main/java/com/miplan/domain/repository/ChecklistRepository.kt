package com.miplan.domain.repository

import com.miplan.domain.model.Checklist
import com.miplan.domain.model.ChecklistItem

/**
 * Interface del repositorio de checklists
 */
interface ChecklistRepository {
    
    /**
     * Obtiene todos los checklists de una tarjeta
     */
    suspend fun getChecklistsByCard(cardId: Int): Result<List<Checklist>>
    
    /**
     * Crea un nuevo checklist
     */
    suspend fun createChecklist(
        cardId: Int,
        title: String
    ): Result<Checklist>
    
    /**
     * Actualiza un checklist existente
     */
    suspend fun updateChecklist(
        id: Int,
        title: String
    ): Result<Checklist>
    
    /**
     * Elimina un checklist
     */
    suspend fun deleteChecklist(id: Int): Result<Unit>
    
    /**
     * Crea un nuevo item en un checklist
     */
    suspend fun createChecklistItem(
        checklistId: Int,
        title: String,
        position: Int? = null
    ): Result<ChecklistItem>
    
    /**
     * Actualiza un item de checklist
     */
    suspend fun updateChecklistItem(
        id: Int,
        title: String? = null,
        isCompleted: Boolean? = null
    ): Result<ChecklistItem>
    
    /**
     * Elimina un item de checklist
     */
    suspend fun deleteChecklistItem(id: Int): Result<Unit>
}
