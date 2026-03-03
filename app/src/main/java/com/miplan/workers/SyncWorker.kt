package com.miplan.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.miplan.data.local.dao.BoardDao
import com.miplan.data.local.dao.CardDao
import com.miplan.data.local.dao.ColumnDao
import com.miplan.data.local.dao.TaskDao
import com.miplan.data.remote.ApiService
import com.miplan.data.remote.dto.request.CreateBoardRequest
import com.miplan.data.remote.dto.request.CreateCardRequest
import com.miplan.data.remote.dto.request.CreateColumnRequest
import com.miplan.data.remote.dto.request.CreateTaskRequest
import com.miplan.data.remote.dto.request.UpdateTaskRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker para sincronizar datos locales con el servidor en background
 * Se ejecuta periódicamente o cuando hay conexión a internet
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val taskDao: TaskDao,
    private val boardDao: BoardDao,
    private val columnDao: ColumnDao,
    private val cardDao: CardDao,
    private val apiService: ApiService
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            // Sincronizar tareas no sincronizadas
            syncTasks()
            
            // Sincronizar tableros no sincronizados
            syncBoards()
            
            // Sincronizar columnas no sincronizadas
            syncColumns()
            
            // Sincronizar tarjetas no sincronizadas
            syncCards()
            
            // Sincronizar eliminaciones
            syncDeletions()
            
            Result.success()
        } catch (e: Exception) {
            // Si falla, reintentar más tarde
            Result.retry()
        }
    }
    
    private suspend fun syncTasks() {
        val unsyncedTasks = taskDao.getUnsyncedTasks()
        
        for (task in unsyncedTasks) {
            try {
                if (task.serverId == null) {
                    // Tarea nueva: crear en servidor
                    val request = CreateTaskRequest(
                        title = task.title,
                        description = task.description,
                        priority = task.priority,
                        dueDate = task.dueDate,
                        imageUrl = task.imageUrl,
                        boardId = task.boardId
                    )
                    
                    val response = apiService.createTask(request)
                    
                    if (response.success && response.data != null) {
                        // Actualizar con ID del servidor
                        taskDao.updateServerIdAndSync(task.localId, response.data.id)
                    }
                } else {
                    // Tarea existente: actualizar en servidor
                    val request = UpdateTaskRequest(
                        title = task.title,
                        description = task.description,
                        status = task.status,
                        priority = task.priority,
                        dueDate = task.dueDate,
                        imageUrl = task.imageUrl,
                        boardId = task.boardId
                    )
                    
                    val response = apiService.updateTask(task.serverId, request)
                    
                    if (response.success) {
                        taskDao.updateSyncStatus(task.localId, true)
                    }
                }
            } catch (e: Exception) {
                // Continuar con la siguiente tarea
                continue
            }
        }
    }
    
    private suspend fun syncBoards() {
        val unsyncedBoards = boardDao.getUnsyncedBoards()
        
        for (board in unsyncedBoards) {
            try {
                if (board.serverId == null) {
                    // Tablero nuevo: crear en servidor
                    val request = CreateBoardRequest(
                        name = board.name,
                        description = board.description,
                        color = board.color,
                        backgroundImageUrl = board.backgroundImageUrl
                    )
                    
                    val response = apiService.createBoard(request)
                    
                    if (response.success && response.data != null) {
                        boardDao.updateServerIdAndSync(board.localId, response.data.id)
                    }
                } else {
                    // Tablero existente: actualizar en servidor
                    val request = CreateBoardRequest(
                        name = board.name,
                        description = board.description,
                        color = board.color,
                        backgroundImageUrl = board.backgroundImageUrl
                    )
                    
                    val response = apiService.updateBoard(board.serverId, request)
                    
                    if (response.success) {
                        boardDao.updateSyncStatus(board.localId, true)
                    }
                }
            } catch (e: Exception) {
                continue
            }
        }
    }
    
    private suspend fun syncColumns() {
        val unsyncedColumns = columnDao.getUnsyncedColumns()
        
        for (column in unsyncedColumns) {
            try {
                if (column.serverId == null) {
                    // Columna nueva: crear en servidor
                    // Necesitamos el serverId del board
                    if (column.boardServerId != null) {
                        val request = CreateColumnRequest(
                            boardId = column.boardServerId,
                            title = column.title,
                            position = column.position
                        )
                        
                        val response = apiService.createColumn(request)
                        
                        if (response.success && response.data != null) {
                            columnDao.updateServerIdAndSync(column.localId, response.data.id)
                        }
                    }
                } else {
                    // Columna existente: actualizar en servidor
                    val request = com.miplan.data.remote.dto.request.UpdateColumnRequest(
                        title = column.title,
                        position = column.position
                    )
                    
                    val response = apiService.updateColumn(column.serverId, request)
                    
                    if (response.success) {
                        columnDao.updateColumn(column.copy(isSynced = true))
                    }
                }
            } catch (e: Exception) {
                continue
            }
        }
    }
    
    private suspend fun syncCards() {
        val unsyncedCards = cardDao.getUnsyncedCards()
        
        for (card in unsyncedCards) {
            try {
                if (card.serverId == null) {
                    // Tarjeta nueva: crear en servidor
                    if (card.columnServerId != null) {
                        val request = CreateCardRequest(
                            columnId = card.columnServerId,
                            title = card.title,
                            description = card.description,
                            taskId = card.taskId,
                            position = card.position,
                            dueDate = card.dueDate,
                            priority = card.priority,
                            labels = card.labels
                        )
                        
                        val response = apiService.createCard(request)
                        
                        if (response.success && response.data != null) {
                            cardDao.updateServerIdAndSync(card.localId, response.data.id)
                        }
                    }
                } else {
                    // Tarjeta existente: actualizar en servidor
                    val request = com.miplan.data.remote.dto.request.UpdateCardRequest(
                        title = card.title,
                        description = card.description,
                        dueDate = card.dueDate,
                        priority = card.priority,
                        labels = card.labels
                    )
                    
                    val response = apiService.updateCard(card.serverId, request)
                    
                    if (response.success) {
                        cardDao.updateCard(card.copy(isSynced = true))
                    }
                }
            } catch (e: Exception) {
                continue
            }
        }
    }
    
    private suspend fun syncDeletions() {
        // Sincronizar tareas eliminadas
        val deletedTasks = taskDao.getDeletedTasks()
        for (task in deletedTasks) {
            try {
                if (task.serverId != null) {
                    val response = apiService.deleteTask(task.serverId)
                    if (response.success) {
                        taskDao.hardDelete(task.localId)
                    }
                } else {
                    // No tiene ID de servidor, eliminar localmente
                    taskDao.hardDelete(task.localId)
                }
            } catch (e: Exception) {
                continue
            }
        }
        
        // Sincronizar tableros eliminados
        val deletedBoards = boardDao.getDeletedBoards()
        for (board in deletedBoards) {
            try {
                if (board.serverId != null) {
                    val response = apiService.deleteBoard(board.serverId)
                    if (response.success) {
                        boardDao.hardDelete(board.localId)
                    }
                } else {
                    boardDao.hardDelete(board.localId)
                }
            } catch (e: Exception) {
                continue
            }
        }
    }
}
