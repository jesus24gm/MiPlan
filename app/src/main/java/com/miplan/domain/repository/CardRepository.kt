package com.miplan.domain.repository

import com.miplan.domain.model.Card

/**
 * Interface del repositorio de tarjetas
 */
interface CardRepository {
    
    /**
     * Obtiene todas las tarjetas de una columna
     */
    suspend fun getCardsByColumn(columnId: Int): Result<List<Card>>
    
    /**
     * Obtiene una tarjeta por su ID
     */
    suspend fun getCardById(id: Int): Result<Card>
    
    /**
     * Crea una nueva tarjeta
     */
    suspend fun createCard(
        columnId: Int,
        title: String,
        description: String? = null,
        position: Int? = null,
        taskId: Int? = null,
        dueDate: String? = null,
        priority: String? = null,
        labels: String? = null
    ): Result<Card>
    
    /**
     * Actualiza una tarjeta existente
     */
    suspend fun updateCard(
        id: Int,
        title: String? = null,
        description: String? = null,
        dueDate: String? = null,
        priority: String? = null,
        labels: String? = null
    ): Result<Card>
    
    /**
     * Elimina una tarjeta
     */
    suspend fun deleteCard(id: Int): Result<Unit>
    
    /**
     * Mueve una tarjeta a una nueva columna y/o posición
     */
    suspend fun moveCard(id: Int, newColumnId: Int, newPosition: Int): Result<Card>
    
    /**
     * Vincula una tarea existente a una tarjeta
     */
    suspend fun linkTaskToCard(cardId: Int, taskId: Int): Result<Card>
    
    /**
     * Desvincula la tarea de una tarjeta
     */
    suspend fun unlinkTaskFromCard(cardId: Int): Result<Card>
    
    /**
     * Crea una tarea desde una tarjeta y la vincula
     */
    suspend fun createTaskFromCard(
        cardId: Int,
        title: String,
        description: String? = null,
        priority: String = "MEDIUM",
        dueDate: String? = null
    ): Result<Card>
}
