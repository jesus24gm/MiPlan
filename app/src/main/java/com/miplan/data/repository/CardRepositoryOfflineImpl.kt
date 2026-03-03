package com.miplan.data.repository

import com.miplan.data.local.dao.CardDao
import com.miplan.data.local.dao.ColumnDao
import com.miplan.data.local.entity.CardEntity
import com.miplan.data.remote.ApiService
import com.miplan.data.remote.dto.request.CreateCardRequest
import com.miplan.data.remote.dto.request.CreateTaskFromCardRequest
import com.miplan.data.remote.dto.request.LinkTaskToCardRequest
import com.miplan.data.remote.dto.request.MoveCardRequest
import com.miplan.data.remote.dto.request.UpdateCardRequest
import com.miplan.domain.model.Card
import com.miplan.domain.repository.CardRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardRepositoryOfflineImpl @Inject constructor(
    private val apiService: ApiService,
    private val cardDao: CardDao,
    private val columnDao: ColumnDao
) : CardRepository {
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    override suspend fun getCardsByColumn(columnId: Int): Result<List<Card>> {
        return try {
            try {
                val response = apiService.getCardsByColumn(columnId)
                if (response.success && response.data != null) {
                    for (cardResponse in response.data) {
                        val serverCard = cardResponse.toDomain()
                        val existingCard = cardDao.getCardByServerId(serverCard.id)
                        
                        if (existingCard != null) {
                            val updatedEntity = existingCard.copy(
                                title = serverCard.title,
                                description = serverCard.description,
                                dueDate = serverCard.dueDate,
                                priority = serverCard.priority,
                                labels = serverCard.labels,
                                position = serverCard.position,
                                taskId = serverCard.taskId,
                                updatedAt = serverCard.updatedAt,
                                isSynced = true
                            )
                            cardDao.updateCard(updatedEntity)
                        } else {
                            val newEntity = CardEntity(
                                serverId = serverCard.id,
                                columnLocalId = columnId,
                                columnServerId = serverCard.columnId,
                                title = serverCard.title,
                                description = serverCard.description,
                                dueDate = serverCard.dueDate,
                                priority = serverCard.priority,
                                labels = serverCard.labels,
                                position = serverCard.position,
                                taskId = serverCard.taskId,
                                createdAt = serverCard.createdAt,
                                updatedAt = serverCard.updatedAt,
                                isSynced = true
                            )
                            cardDao.insertCard(newEntity)
                        }
                    }
                }
            } catch (e: Exception) {
                // Continuar con datos locales
            }
            
            val localCards = cardDao.getCardsByColumnLocalId(columnId).first().map { it.toDomain() }
            Result.success(localCards)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCardById(id: Int): Result<Card> {
        return try {
            var localCard = cardDao.getCardByServerId(id)
            if (localCard == null) {
                localCard = cardDao.getCardByLocalId(id)
            }
            
            if (localCard != null) {
                if (localCard.serverId != null) {
                    try {
                        val response = apiService.getCardById(localCard.serverId)
                        if (response.success && response.data != null) {
                            val serverCard = response.data.toDomain()
                            val updatedEntity = localCard.copy(
                                title = serverCard.title,
                                description = serverCard.description,
                                dueDate = serverCard.dueDate,
                                priority = serverCard.priority,
                                labels = serverCard.labels,
                                position = serverCard.position,
                                taskId = serverCard.taskId,
                                updatedAt = serverCard.updatedAt,
                                isSynced = true
                            )
                            cardDao.updateCard(updatedEntity)
                            return Result.success(updatedEntity.toDomain())
                        }
                    } catch (e: Exception) {
                        // Ignorar errores de red
                    }
                }
                Result.success(localCard.toDomain())
            } else {
                try {
                    val response = apiService.getCardById(id)
                    if (response.success && response.data != null) {
                        val serverCard = response.data.toDomain()
                        val newEntity = CardEntity(
                            serverId = serverCard.id,
                            columnLocalId = serverCard.columnId,
                            columnServerId = serverCard.columnId,
                            title = serverCard.title,
                            description = serverCard.description,
                            dueDate = serverCard.dueDate,
                            priority = serverCard.priority,
                            labels = serverCard.labels,
                            position = serverCard.position,
                            taskId = serverCard.taskId,
                            createdAt = serverCard.createdAt,
                            updatedAt = serverCard.updatedAt,
                            isSynced = true
                        )
                        cardDao.insertCard(newEntity)
                        Result.success(newEntity.toDomain())
                    } else {
                        Result.failure(Exception("Tarjeta no encontrada"))
                    }
                } catch (e: Exception) {
                    Result.failure(Exception("Tarjeta no encontrada"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createCard(
        columnId: Int,
        title: String,
        description: String?,
        position: Int?,
        taskId: Int?,
        dueDate: String?,
        priority: String?,
        labels: String?
    ): Result<Card> {
        return try {
            val now = LocalDateTime.now().format(dateFormatter)
            
            // Obtener el columnServerId de la columna
            val column = columnDao.getColumnByLocalId(columnId) ?: columnDao.getColumnByServerId(columnId)
            
            val localEntity = CardEntity(
                serverId = null,
                columnLocalId = columnId,
                columnServerId = column?.serverId,
                title = title,
                description = description,
                dueDate = dueDate,
                priority = priority,
                labels = labels,
                position = position ?: 0,
                taskId = taskId,
                createdAt = now,
                updatedAt = now,
                isSynced = false
            )
            
            val localId = cardDao.insertCard(localEntity).toInt()
            val savedEntity = localEntity.copy(localId = localId)
            
            try {
                val request = CreateCardRequest(
                    columnId = columnId,
                    title = title,
                    description = description,
                    taskId = taskId,
                    position = position,
                    dueDate = dueDate,
                    priority = priority,
                    labels = labels
                )
                
                val response = apiService.createCard(request)
                
                if (response.success && response.data != null) {
                    val serverCard = response.data.toDomain()
                    cardDao.updateServerIdAndSync(localId, serverCard.id)
                    
                    val updatedEntity = savedEntity.copy(
                        serverId = serverCard.id,
                        columnServerId = serverCard.columnId,
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
    
    override suspend fun updateCard(
        id: Int,
        title: String?,
        description: String?,
        dueDate: String?,
        priority: String?,
        labels: String?
    ): Result<Card> {
        return try {
            var localCard = cardDao.getCardByServerId(id)
            if (localCard == null) {
                localCard = cardDao.getCardByLocalId(id)
            }
            
            if (localCard != null) {
                val now = LocalDateTime.now().format(dateFormatter)
                val updatedEntity = localCard.copy(
                    title = title ?: localCard.title,
                    description = description ?: localCard.description,
                    dueDate = dueDate ?: localCard.dueDate,
                    priority = priority ?: localCard.priority,
                    labels = labels ?: localCard.labels,
                    updatedAt = now,
                    isSynced = false
                )
                
                cardDao.updateCard(updatedEntity)
                
                try {
                    if (localCard.serverId != null) {
                        val request = UpdateCardRequest(
                            title = title,
                            description = description,
                            dueDate = dueDate,
                            priority = priority,
                            labels = labels
                        )
                        
                        val response = apiService.updateCard(localCard.serverId, request)
                        
                        if (response.success) {
                            val syncedEntity = updatedEntity.copy(isSynced = true)
                            cardDao.updateCard(syncedEntity)
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
                Result.failure(Exception("Tarjeta no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteCard(id: Int): Result<Unit> {
        return try {
            var localCard = cardDao.getCardByServerId(id)
            if (localCard == null) {
                localCard = cardDao.getCardByLocalId(id)
            }
            
            if (localCard != null) {
                cardDao.softDelete(localCard.localId)
                
                try {
                    if (localCard.serverId != null) {
                        val response = apiService.deleteCard(localCard.serverId)
                        if (response.success) {
                            cardDao.hardDelete(localCard.localId)
                        }
                    } else {
                        cardDao.hardDelete(localCard.localId)
                    }
                } catch (e: Exception) {
                    // Error de red
                }
                
                Result.success(Unit)
            } else {
                Result.failure(Exception("Tarjeta no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun moveCard(id: Int, newColumnId: Int, newPosition: Int): Result<Card> {
        return try {
            var localCard = cardDao.getCardByServerId(id)
            if (localCard == null) {
                localCard = cardDao.getCardByLocalId(id)
            }
            
            if (localCard != null) {
                cardDao.moveCard(localCard.localId, newColumnId, newPosition)
                
                try {
                    if (localCard.serverId != null) {
                        val request = MoveCardRequest(
                            newColumnId = newColumnId,
                            newPosition = newPosition
                        )
                        
                        val response = apiService.moveCard(localCard.serverId, request)
                        
                        if (response.success && response.data != null) {
                            val serverCard = response.data.toDomain()
                            val updatedEntity = localCard.copy(
                                columnLocalId = newColumnId,
                                columnServerId = serverCard.columnId,
                                position = newPosition,
                                isSynced = true
                            )
                            cardDao.updateCard(updatedEntity)
                            Result.success(updatedEntity.toDomain())
                        } else {
                            val updatedCard = cardDao.getCardByLocalId(localCard.localId)
                            Result.success(updatedCard!!.toDomain())
                        }
                    } else {
                        val updatedCard = cardDao.getCardByLocalId(localCard.localId)
                        Result.success(updatedCard!!.toDomain())
                    }
                } catch (e: Exception) {
                    val updatedCard = cardDao.getCardByLocalId(localCard.localId)
                    Result.success(updatedCard!!.toDomain())
                }
            } else {
                Result.failure(Exception("Tarjeta no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun linkTaskToCard(cardId: Int, taskId: Int): Result<Card> {
        return try {
            var localCard = cardDao.getCardByServerId(cardId)
            if (localCard == null) {
                localCard = cardDao.getCardByLocalId(cardId)
            }
            
            if (localCard != null) {
                val updatedEntity = localCard.copy(
                    taskId = taskId,
                    isSynced = false
                )
                cardDao.updateCard(updatedEntity)
                
                try {
                    if (localCard.serverId != null) {
                        val request = LinkTaskToCardRequest(taskId = taskId)
                        val response = apiService.linkTaskToCard(localCard.serverId, request)
                        
                        if (response.success && response.data != null) {
                            val syncedEntity = updatedEntity.copy(isSynced = true)
                            cardDao.updateCard(syncedEntity)
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
                Result.failure(Exception("Tarjeta no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun unlinkTaskFromCard(cardId: Int): Result<Card> {
        return try {
            var localCard = cardDao.getCardByServerId(cardId)
            if (localCard == null) {
                localCard = cardDao.getCardByLocalId(cardId)
            }
            
            if (localCard != null) {
                val updatedEntity = localCard.copy(
                    taskId = null,
                    isSynced = false
                )
                cardDao.updateCard(updatedEntity)
                
                try {
                    if (localCard.serverId != null) {
                        val response = apiService.unlinkTaskFromCard(localCard.serverId)
                        
                        if (response.success && response.data != null) {
                            val syncedEntity = updatedEntity.copy(isSynced = true)
                            cardDao.updateCard(syncedEntity)
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
                Result.failure(Exception("Tarjeta no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createTaskFromCard(
        cardId: Int,
        title: String,
        description: String?,
        priority: String,
        dueDate: String?
    ): Result<Card> {
        return try {
            var localCard = cardDao.getCardByServerId(cardId)
            if (localCard == null) {
                localCard = cardDao.getCardByLocalId(cardId)
            }
            
            if (localCard != null) {
                try {
                    if (localCard.serverId != null) {
                        val request = CreateTaskFromCardRequest(
                            title = title,
                            description = description,
                            priority = priority,
                            dueDate = dueDate
                        )
                        
                        val response = apiService.createTaskFromCard(localCard.serverId, request)
                        
                        if (response.success && response.data != null) {
                            val serverCard = response.data.toDomain()
                            val updatedEntity = localCard.copy(
                                taskId = serverCard.taskId,
                                isSynced = true
                            )
                            cardDao.updateCard(updatedEntity)
                            Result.success(updatedEntity.toDomain())
                        } else {
                            Result.failure(Exception(response.message))
                        }
                    } else {
                        Result.failure(Exception("No se puede crear tarea desde tarjeta no sincronizada"))
                    }
                } catch (e: Exception) {
                    Result.failure(e)
                }
            } else {
                Result.failure(Exception("Tarjeta no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
