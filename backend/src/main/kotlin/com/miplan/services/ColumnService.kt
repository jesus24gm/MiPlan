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
    suspend fun createColumn(request: CreateColumnRequest): ColumnResponse? {
        try {
            println("DEBUG: Creando columna - boardId: ${request.boardId}, title: ${request.title}, position: ${request.position}")
            
            val column = columnRepository.create(
                boardId = request.boardId,
                title = request.title,
                position = request.position
            )
            
            if (column == null) {
                println("ERROR: columnRepository.create() devolvió null")
                return null
            }
            
            println("DEBUG: Columna creada exitosamente - id: ${column.id}")
            return columnToResponse(column)
        } catch (e: Exception) {
            println("ERROR en createColumn: ${e.message}")
            e.printStackTrace()
            return null
        }
    }
    
    /**
     * Obtener columna por ID
     */
    suspend fun getColumnById(id: Int): ColumnResponse? {
        val column = columnRepository.findById(id) ?: return null
        return columnToResponse(column)
    }
    
    /**
     * Obtener todas las columnas de un tablero
     */
    suspend fun getColumnsByBoardId(boardId: Int): List<ColumnResponse> {
        val columns = columnRepository.findByBoardId(boardId)
        return columns.map { columnToResponse(it) }
    }
    
    /**
     * Actualizar columna
     */
    suspend fun updateColumn(id: Int, request: UpdateColumnRequest): ColumnResponse? {
        val updated = columnRepository.update(
            id = id,
            title = request.title,
            position = request.position
        )
        
        if (!updated) return null
        
        return columnRepository.findById(id)?.let { columnToResponse(it) }
    }
    
    /**
     * Eliminar columna
     */
    suspend fun deleteColumn(id: Int): Boolean {
        return columnRepository.delete(id)
    }
    
    /**
     * Mover columna a nueva posición
     */
    suspend fun moveColumn(id: Int, request: MoveColumnRequest): ColumnResponse? {
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
