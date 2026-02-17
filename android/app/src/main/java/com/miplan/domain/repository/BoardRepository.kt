package com.miplan.domain.repository

import com.miplan.domain.model.Board

/**
 * Interface del repositorio de tableros
 */
interface BoardRepository {
    
    /**
     * Obtiene todos los tableros del usuario actual
     */
    suspend fun getBoards(): Result<List<Board>>
    
    /**
     * Obtiene un tablero por su ID
     */
    suspend fun getBoardById(id: Int): Result<Board>
    
    /**
     * Crea un nuevo tablero
     */
    suspend fun createBoard(
        name: String,
        description: String?,
        color: String
    ): Result<Board>
    
    /**
     * Actualiza un tablero existente
     */
    suspend fun updateBoard(
        id: Int,
        name: String,
        description: String?,
        color: String
    ): Result<Board>
    
    /**
     * Elimina un tablero
     */
    suspend fun deleteBoard(id: Int): Result<Unit>
}
