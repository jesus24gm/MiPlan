package com.miplan.data.repository

import com.miplan.data.local.dao.BoardDao
import com.miplan.data.local.dao.CardDao
import com.miplan.data.local.dao.ColumnDao
import com.miplan.data.local.entity.ColumnEntity
import com.miplan.data.remote.ApiService
import com.miplan.data.remote.dto.request.CreateColumnRequest
import com.miplan.data.remote.dto.request.UpdateColumnRequest
import com.miplan.domain.model.Column
import com.miplan.domain.repository.ColumnRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio de columnas con soporte offline
 */
@Singleton
class ColumnRepositoryOfflineImpl @Inject constructor(
    private val apiService: ApiService,
    private val columnDao: ColumnDao,
    private val boardDao: BoardDao,
    private val cardDao: CardDao
) : ColumnRepository {
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    override suspend fun getColumnsByBoard(boardId: Int): Result<List<Column>> {
        return try {
            // Intentar sincronizar con servidor
            try {
                val response = apiService.getColumnsByBoard(boardId)
                if (response.success && response.data != null) {
                    for (columnResponse in response.data) {
                        val serverColumn = columnResponse.toDomain()
                        val existingColumn = columnDao.getColumnByServerId(serverColumn.id)
                        
                        if (existingColumn != null) {
                            val updatedEntity = existingColumn.copy(
                                title = serverColumn.title,
                                position = serverColumn.position,
                                updatedAt = serverColumn.updatedAt,
                                isSynced = true
                            )
                            columnDao.updateColumn(updatedEntity)
                        } else {
                            val newEntity = ColumnEntity(
                                serverId = serverColumn.id,
                                boardLocalId = boardId, // Usar el boardId que puede ser localId
                                boardServerId = serverColumn.boardId,
                                title = serverColumn.title,
                                position = serverColumn.position,
                                createdAt = serverColumn.createdAt,
                                updatedAt = serverColumn.updatedAt,
                                isSynced = true
                            )
                            columnDao.insertColumn(newEntity)
                        }
                    }
                }
            } catch (e: Exception) {
                // Continuar con datos locales
            }
            
            // Buscar por boardLocalId (que es el ID que usamos en la UI)
            val localColumns = columnDao.getColumnsByBoardLocalId(boardId).first()
            
            // Cargar las tarjetas de cada columna
            val columnsWithCards = localColumns.map { columnEntity ->
                val cards = cardDao.getCardsByColumnLocalId(columnEntity.localId).first().map { it.toDomain() }
                columnEntity.toDomain().copy(cards = cards)
            }
            
            Result.success(columnsWithCards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createColumn(boardId: Int, title: String, position: Int?): Result<Column> {
        return try {
            val now = LocalDateTime.now().format(dateFormatter)
            
            // Obtener el boardServerId del tablero
            val board = boardDao.getBoardByLocalId(boardId) ?: boardDao.getBoardByServerId(boardId)
            
            // Crear columna local
            val localEntity = ColumnEntity(
                serverId = null,
                boardLocalId = boardId,
                boardServerId = board?.serverId, // Usar serverId del board si existe
                title = title,
                position = position ?: 0,
                createdAt = now,
                updatedAt = now,
                isSynced = false
            )
            
            val localId = columnDao.insertColumn(localEntity).toInt()
            val savedEntity = localEntity.copy(localId = localId)
            
            // Intentar sincronizar con servidor
            try {
                val request = CreateColumnRequest(
                    boardId = boardId,
                    title = title,
                    position = position
                )
                
                val response = apiService.createColumn(request)
                
                if (response.success && response.data != null) {
                    val serverColumn = response.data.toDomain()
                    columnDao.updateServerIdAndSync(localId, serverColumn.id)
                    
                    val updatedEntity = savedEntity.copy(
                        serverId = serverColumn.id,
                        boardServerId = serverColumn.boardId,
                        isSynced = true
                    )
                    Result.success(updatedEntity.toDomain())
                } else {
                    Result.success(savedEntity.toDomain())
                }
            } catch (e: Exception) {
                Result.success(savedEntity.toDomain())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateColumn(id: Int, title: String?, position: Int?): Result<Column> {
        return try {
            var localColumn = columnDao.getColumnByServerId(id)
            if (localColumn == null) {
                localColumn = columnDao.getColumnByLocalId(id)
            }
            
            if (localColumn != null) {
                val now = LocalDateTime.now().format(dateFormatter)
                val updatedEntity = localColumn.copy(
                    title = title ?: localColumn.title,
                    position = position ?: localColumn.position,
                    updatedAt = now,
                    isSynced = false
                )
                
                columnDao.updateColumn(updatedEntity)
                
                // Intentar sincronizar
                try {
                    if (localColumn.serverId != null) {
                        val request = UpdateColumnRequest(
                            title = title,
                            position = position
                        )
                        
                        val response = apiService.updateColumn(localColumn.serverId, request)
                        
                        if (response.success) {
                            val syncedEntity = updatedEntity.copy(isSynced = true)
                            columnDao.updateColumn(syncedEntity)
                            Result.success(syncedEntity.toDomain())
                        } else {
                            Result.success(updatedEntity.toDomain())
                        }
                    } else {
                        Result.success(updatedEntity.toDomain())
                    }
                } catch (e: Exception) {
                    Result.success(updatedEntity.toDomain())
                }
            } else {
                Result.failure(Exception("Columna no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteColumn(id: Int): Result<Unit> {
        return try {
            var localColumn = columnDao.getColumnByServerId(id)
            if (localColumn == null) {
                localColumn = columnDao.getColumnByLocalId(id)
            }
            
            if (localColumn != null) {
                columnDao.softDelete(localColumn.localId)
                
                try {
                    if (localColumn.serverId != null) {
                        val response = apiService.deleteColumn(localColumn.serverId)
                        if (response.success) {
                            columnDao.hardDelete(localColumn.localId)
                        }
                    } else {
                        columnDao.hardDelete(localColumn.localId)
                    }
                } catch (e: Exception) {
                    // Error de red, quedará marcada para eliminación posterior
                }
                
                Result.success(Unit)
            } else {
                Result.failure(Exception("Columna no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun moveColumn(id: Int, newPosition: Int): Result<Column> {
        return try {
            var localColumn = columnDao.getColumnByServerId(id)
            if (localColumn == null) {
                localColumn = columnDao.getColumnByLocalId(id)
            }
            
            if (localColumn != null) {
                val now = LocalDateTime.now().format(dateFormatter)
                val updatedEntity = localColumn.copy(
                    position = newPosition,
                    updatedAt = now,
                    isSynced = false
                )
                
                columnDao.updateColumn(updatedEntity)
                
                // Intentar sincronizar
                try {
                    if (localColumn.serverId != null) {
                        val request = com.miplan.data.remote.dto.request.MoveColumnRequest(
                            newPosition = newPosition
                        )
                        
                        val response = apiService.moveColumn(localColumn.serverId, request)
                        
                        if (response.success && response.data != null) {
                            val syncedEntity = updatedEntity.copy(isSynced = true)
                            columnDao.updateColumn(syncedEntity)
                            Result.success(syncedEntity.toDomain())
                        } else {
                            Result.success(updatedEntity.toDomain())
                        }
                    } else {
                        Result.success(updatedEntity.toDomain())
                    }
                } catch (e: Exception) {
                    Result.success(updatedEntity.toDomain())
                }
            } else {
                Result.failure(Exception("Columna no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
