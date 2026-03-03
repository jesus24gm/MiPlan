package com.miplan.data.repository

import com.miplan.data.local.dao.BoardDao
import com.miplan.data.local.entity.BoardEntity
import com.miplan.data.remote.ApiService
import com.miplan.data.remote.dto.request.CreateBoardRequest
import com.miplan.domain.model.Board
import com.miplan.domain.repository.BoardRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio de tableros con soporte offline
 */
@Singleton
class BoardRepositoryOfflineImpl @Inject constructor(
    private val apiService: ApiService,
    private val boardDao: BoardDao
) : BoardRepository {
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    override suspend fun getBoards(): Result<List<Board>> {
        return try {
            // Intentar sincronizar con servidor
            try {
                val response = apiService.getBoards()
                if (response.success && response.data != null) {
                    // Actualizar tableros existentes o insertar nuevos
                    for (boardResponse in response.data) {
                        val serverBoard = boardResponse.toDomain()
                        val existingBoard = boardDao.getBoardByServerId(serverBoard.id)
                        
                        if (existingBoard != null) {
                            // Actualizar tablero existente manteniendo el localId
                            val updatedEntity = existingBoard.copy(
                                name = serverBoard.name,
                                description = serverBoard.description,
                                color = serverBoard.color,
                                backgroundImageUrl = serverBoard.backgroundImageUrl,
                                userId = serverBoard.userId,
                                updatedAt = serverBoard.updatedAt,
                                isSynced = true
                            )
                            boardDao.updateBoard(updatedEntity)
                        } else {
                            // Nuevo tablero del servidor
                            val newEntity = BoardEntity(
                                serverId = serverBoard.id,
                                name = serverBoard.name,
                                description = serverBoard.description,
                                color = serverBoard.color,
                                backgroundImageUrl = serverBoard.backgroundImageUrl,
                                userId = serverBoard.userId,
                                createdAt = serverBoard.createdAt,
                                updatedAt = serverBoard.updatedAt,
                                isSynced = true
                            )
                            boardDao.insertBoard(newEntity)
                        }
                    }
                }
            } catch (e: Exception) {
                // Continuar con datos locales
            }
            
            val localBoards = boardDao.getAllBoards().first().map { it.toDomain() }
            Result.success(localBoards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getBoardById(id: Int): Result<Board> {
        return try {
            val localBoard = boardDao.getBoardByServerId(id)
            
            if (localBoard != null) {
                // Intentar actualizar desde servidor
                try {
                    val response = apiService.getBoardById(id)
                    if (response.success && response.data != null) {
                        val entity = BoardEntity.fromDomain(response.data.toDomain(), isSynced = true)
                        boardDao.insertBoard(entity)
                        return Result.success(entity.toDomain())
                    }
                } catch (e: Exception) {
                    // Ignorar errores de red
                }
                
                Result.success(localBoard.toDomain())
            } else {
                val response = apiService.getBoardById(id)
                if (response.success && response.data != null) {
                    val board = response.data.toDomain()
                    val entity = BoardEntity.fromDomain(board, isSynced = true)
                    boardDao.insertBoard(entity)
                    Result.success(board)
                } else {
                    Result.failure(Exception(response.message))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createBoard(
        name: String,
        description: String?,
        color: String,
        backgroundImageUrl: String?
    ): Result<Board> {
        return try {
            val now = LocalDateTime.now().format(dateFormatter)
            
            // Crear entidad local
            val localEntity = BoardEntity(
                serverId = null,
                name = name,
                description = description,
                color = color,
                backgroundImageUrl = backgroundImageUrl,
                userId = null,
                createdAt = now,
                updatedAt = now,
                isSynced = false
            )
            
            // Guardar localmente
            val localId = boardDao.insertBoard(localEntity).toInt()
            val savedEntity = localEntity.copy(localId = localId)
            
            // Intentar sincronizar con servidor
            try {
                val request = CreateBoardRequest(
                    name = name,
                    description = description,
                    color = color,
                    backgroundImageUrl = backgroundImageUrl
                )
                
                val response = apiService.createBoard(request)
                
                if (response.success && response.data != null) {
                    val serverBoard = response.data.toDomain()
                    boardDao.updateServerIdAndSync(localId, serverBoard.id)
                    Result.success(serverBoard)
                } else {
                    Result.success(savedEntity.toDomain())
                }
            } catch (e: Exception) {
                // Error de red, tablero guardado localmente
                Result.success(savedEntity.toDomain())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateBoard(
        id: Int,
        name: String,
        description: String?,
        color: String,
        backgroundImageUrl: String?
    ): Result<Board> {
        return try {
            val localBoard = boardDao.getBoardByServerId(id) ?: boardDao.getBoardByLocalId(id)
            
            if (localBoard != null) {
                val now = LocalDateTime.now().format(dateFormatter)
                val updatedEntity = localBoard.copy(
                    name = name,
                    description = description,
                    color = color,
                    backgroundImageUrl = backgroundImageUrl,
                    updatedAt = now,
                    isSynced = false
                )
                
                boardDao.updateBoard(updatedEntity)
                
                // Intentar sincronizar
                try {
                    val request = CreateBoardRequest(
                        name = name,
                        description = description,
                        color = color,
                        backgroundImageUrl = backgroundImageUrl
                    )
                    
                    val serverId = localBoard.serverId ?: id
                    val response = apiService.updateBoard(serverId, request)
                    
                    if (response.success && response.data != null) {
                        boardDao.updateSyncStatus(localBoard.localId, true)
                        Result.success(response.data.toDomain())
                    } else {
                        Result.success(updatedEntity.toDomain())
                    }
                } catch (e: Exception) {
                    Result.success(updatedEntity.toDomain())
                }
            } else {
                Result.failure(Exception("Tablero no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteBoard(id: Int): Result<Unit> {
        return try {
            val localBoard = boardDao.getBoardByServerId(id) ?: boardDao.getBoardByLocalId(id)
            
            if (localBoard != null) {
                boardDao.softDelete(localBoard.localId)
                
                try {
                    val serverId = localBoard.serverId ?: id
                    val response = apiService.deleteBoard(serverId)
                    
                    if (response.success) {
                        boardDao.hardDelete(localBoard.localId)
                    }
                } catch (e: Exception) {
                    // Error de red, quedará marcado para eliminación posterior
                }
                
                Result.success(Unit)
            } else {
                Result.failure(Exception("Tablero no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
