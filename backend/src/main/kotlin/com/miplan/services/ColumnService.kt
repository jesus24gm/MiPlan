package com.miplan.services

import com.miplan.models.Column
import com.miplan.models.requests.CreateColumnRequest
import com.miplan.models.requests.MoveColumnRequest
import com.miplan.models.requests.UpdateColumnRequest
import com.miplan.models.responses.ColumnResponse
import com.miplan.repositories.ColumnRepository
import java.time.format.DateTimeFormatter

/**
 * Servicio para lógica de negocio de columnas
 */
class ColumnService(
    private val columnRepository: ColumnRepository
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    
    /**
     * Crear una nueva columna
     */
    fun createColumn(request: CreateColumnRequest): ColumnResponse {
        val column = columnRepository.create(
            boardId = request.boardId,
            title = request.title,
            position = request.position
        )
        
        return columnToResponse(column)
    }
    
    /**
     * Obtener columna por ID
     */
    fun getColumnById(id: Int): ColumnResponse? {
        val column = columnRepository.findById(id) ?: return null
        return columnToResponse(column)
    }
    
    /**
     * Obtener todas las columnas de un tablero
     */
    fun getColumnsByBoardId(boardId: Int): List<ColumnResponse> {
        val columns = columnRepository.findByBoardId(boardId)
        return columns.map { columnToResponse(it) }
    }
    
    /**
     * Actualizar columna
     */
    fun updateColumn(id: Int, request: UpdateColumnRequest): ColumnResponse? {
        val column = columnRepository.update(
            id = id,
            title = request.title,
            position = request.position
        ) ?: return null
        
        return columnToResponse(column)
    }
    
    /**
     * Eliminar columna
     */
    fun deleteColumn(id: Int): Boolean {
        return columnRepository.delete(id)
    }
    
    /**
     * Mover columna a nueva posición
     */
    fun moveColumn(id: Int, request: MoveColumnRequest): ColumnResponse? {
        val column = columnRepository.moveColumn(id, request.newPosition) ?: return null
        return columnToResponse(column)
    }
    
    /**
     * Mapear Column a ColumnResponse
     */
    private fun columnToResponse(column: Column): ColumnResponse {
        return ColumnResponse(
            id = column.id,
            boardId = column.boardId,
            title = column.title,
            position = column.position,
            createdAt = column.createdAt.format(dateFormatter),
            updatedAt = column.updatedAt.format(dateFormatter)
        )
    }
}
