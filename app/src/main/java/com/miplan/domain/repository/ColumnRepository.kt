package com.miplan.domain.repository

import com.miplan.domain.model.Column

/**
 * Interface del repositorio de columnas
 */
interface ColumnRepository {
    
    /**
     * Obtiene todas las columnas de un tablero
     */
    suspend fun getColumnsByBoard(boardId: Int): Result<List<Column>>
    
    /**
     * Crea una nueva columna
     */
    suspend fun createColumn(
        boardId: Int,
        title: String,
        position: Int? = null
    ): Result<Column>
    
    /**
     * Actualiza una columna existente
     */
    suspend fun updateColumn(
        id: Int,
        title: String?,
        position: Int?
    ): Result<Column>
    
    /**
     * Elimina una columna
     */
    suspend fun deleteColumn(id: Int): Result<Unit>
    
    /**
     * Mueve una columna a una nueva posición
     */
    suspend fun moveColumn(id: Int, newPosition: Int): Result<Column>
}
