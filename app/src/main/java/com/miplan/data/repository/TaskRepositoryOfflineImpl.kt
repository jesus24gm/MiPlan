package com.miplan.data.repository

import com.miplan.data.local.dao.TaskDao
import com.miplan.data.local.entity.TaskEntity
import com.miplan.data.remote.ApiService
import com.miplan.data.remote.dto.request.CreateTaskRequest
import com.miplan.data.remote.dto.request.UpdateTaskRequest
import com.miplan.domain.model.Task
import com.miplan.domain.model.TaskPriority
import com.miplan.domain.model.TaskStatus
import com.miplan.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio de tareas con soporte offline
 * Estrategia: Offline-first con sincronización automática
 */
@Singleton
class TaskRepositoryOfflineImpl @Inject constructor(
    private val apiService: ApiService,
    private val taskDao: TaskDao
) : TaskRepository {
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    /**
     * Obtiene todas las tareas
     * 1. Retorna datos locales inmediatamente
     * 2. Intenta sincronizar con el servidor en background
     */
    override suspend fun getTasks(): Result<List<Task>> {
        return try {
            // Intentar obtener del servidor y actualizar cache local
            try {
                val response = apiService.getTasks()
                if (response.success && response.data != null) {
                    // Actualizar cache local: buscar tareas existentes por serverId
                    for (taskResponse in response.data) {
                        val serverTask = taskResponse.toDomain()
                        val existingTask = taskDao.getTaskByServerId(serverTask.id)
                        
                        if (existingTask != null) {
                            // Actualizar tarea existente manteniendo el localId
                            val updatedEntity = existingTask.copy(
                                title = serverTask.title,
                                description = serverTask.description,
                                status = serverTask.status.name,
                                priority = serverTask.priority.name,
                                dueDate = serverTask.dueDate,
                                imageUrl = serverTask.imageUrl,
                                boardId = serverTask.boardId,
                                createdBy = serverTask.createdBy,
                                updatedAt = serverTask.updatedAt,
                                isSynced = true
                            )
                            taskDao.updateTask(updatedEntity)
                        } else {
                            // Nueva tarea del servidor
                            val newEntity = TaskEntity(
                                serverId = serverTask.id,
                                title = serverTask.title,
                                description = serverTask.description,
                                status = serverTask.status.name,
                                priority = serverTask.priority.name,
                                dueDate = serverTask.dueDate,
                                imageUrl = serverTask.imageUrl,
                                boardId = serverTask.boardId,
                                createdBy = serverTask.createdBy,
                                createdAt = serverTask.createdAt,
                                updatedAt = serverTask.updatedAt,
                                isSynced = true
                            )
                            taskDao.insertTask(newEntity)
                        }
                    }
                }
            } catch (e: Exception) {
                // Si falla la red, continuamos con datos locales
            }
            
            // Retornar datos locales (siempre disponibles)
            val localTasks = taskDao.getAllTasks().first().map { it.toDomain() }
            Result.success(localTasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTaskById(id: Int): Result<Task> {
        return try {
            // Buscar primero por serverId, luego por localId
            var localTask = taskDao.getTaskByServerId(id)
            
            if (localTask == null) {
                // Si no se encuentra por serverId, buscar por localId
                localTask = taskDao.getTaskByLocalId(id)
            }
            
            if (localTask != null) {
                // Si la tarea tiene serverId, intentar actualizar desde servidor
                if (localTask.serverId != null) {
                    try {
                        val response = apiService.getTaskById(localTask.serverId)
                        if (response.success && response.data != null) {
                            val serverTask = response.data.toDomain()
                            // Actualizar tarea existente manteniendo el localId
                            val updatedEntity = localTask.copy(
                                title = serverTask.title,
                                description = serverTask.description,
                                status = serverTask.status.name,
                                priority = serverTask.priority.name,
                                dueDate = serverTask.dueDate,
                                imageUrl = serverTask.imageUrl,
                                boardId = serverTask.boardId,
                                createdBy = serverTask.createdBy,
                                updatedAt = serverTask.updatedAt,
                                isSynced = true
                            )
                            taskDao.updateTask(updatedEntity)
                            return Result.success(updatedEntity.toDomain())
                        }
                    } catch (e: Exception) {
                        // Ignorar errores de red, retornar datos locales
                    }
                }
                
                // Retornar datos locales
                Result.success(localTask.toDomain())
            } else {
                // No está en cache local, intentar desde servidor
                try {
                    val response = apiService.getTaskById(id)
                    if (response.success && response.data != null) {
                        val serverTask = response.data.toDomain()
                        // Crear nueva tarea con serverId
                        val newEntity = TaskEntity(
                            serverId = serverTask.id,
                            title = serverTask.title,
                            description = serverTask.description,
                            status = serverTask.status.name,
                            priority = serverTask.priority.name,
                            dueDate = serverTask.dueDate,
                            imageUrl = serverTask.imageUrl,
                            boardId = serverTask.boardId,
                            createdBy = serverTask.createdBy,
                            createdAt = serverTask.createdAt,
                            updatedAt = serverTask.updatedAt,
                            isSynced = true
                        )
                        taskDao.insertTask(newEntity)
                        Result.success(newEntity.toDomain())
                    } else {
                        Result.failure(Exception("Tarea no encontrada"))
                    }
                } catch (e: Exception) {
                    Result.failure(Exception("Tarea no encontrada"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTasksByBoard(boardId: Int): Result<List<Task>> {
        return try {
            // Intentar sincronizar con servidor
            try {
                val response = apiService.getTasksByBoard(boardId)
                if (response.success && response.data != null) {
                    val entities = response.data.map { TaskEntity.fromDomain(it.toDomain(), isSynced = true) }
                    taskDao.insertTasks(entities)
                }
            } catch (e: Exception) {
                // Continuar con datos locales
            }
            
            val localTasks = taskDao.getTasksByBoard(boardId).first().map { it.toDomain() }
            Result.success(localTasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTasksByStatus(status: TaskStatus): Result<List<Task>> {
        return try {
            // Intentar sincronizar
            try {
                val response = apiService.getTasksByStatus(status.name)
                if (response.success && response.data != null) {
                    val entities = response.data.map { TaskEntity.fromDomain(it.toDomain(), isSynced = true) }
                    taskDao.insertTasks(entities)
                }
            } catch (e: Exception) {
                // Continuar con datos locales
            }
            
            val localTasks = taskDao.getTasksByStatus(status.name).first().map { it.toDomain() }
            Result.success(localTasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTasksByDate(date: String): Result<List<Task>> {
        return try {
            // Intentar sincronizar
            try {
                val response = apiService.getTasksByDate(date)
                if (response.success && response.data != null) {
                    val entities = response.data.map { TaskEntity.fromDomain(it.toDomain(), isSynced = true) }
                    taskDao.insertTasks(entities)
                }
            } catch (e: Exception) {
                // Continuar con datos locales
            }
            
            val localTasks = taskDao.getTasksByDate(date).first().map { it.toDomain() }
            Result.success(localTasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Crea una tarea con estrategia offline-first
     * 1. Guarda localmente primero (siempre funciona)
     * 2. Intenta sincronizar con servidor
     * 3. Si falla, queda marcada como no sincronizada para retry posterior
     */
    override suspend fun createTask(
        title: String,
        description: String?,
        priority: TaskPriority,
        dueDate: String?,
        imageUrl: String?,
        boardId: Int?
    ): Result<Task> {
        return try {
            val now = LocalDateTime.now().format(dateFormatter)
            
            // 1. Crear entidad local
            val localEntity = TaskEntity(
                serverId = null, // No tiene ID de servidor aún
                title = title,
                description = description,
                status = TaskStatus.PENDING.name,
                priority = priority.name,
                dueDate = dueDate,
                imageUrl = imageUrl,
                boardId = boardId,
                createdBy = null, // Se actualizará con la respuesta del servidor
                createdAt = now,
                updatedAt = now,
                isSynced = false // Marcada como no sincronizada
            )
            
            // 2. Guardar localmente (SIEMPRE funciona)
            val localId = taskDao.insertTask(localEntity).toInt()
            val savedEntity = localEntity.copy(localId = localId)
            
            // 3. Intentar sincronizar con servidor
            try {
                val request = CreateTaskRequest(
                    title = title,
                    description = description,
                    priority = priority.name,
                    dueDate = dueDate,
                    imageUrl = imageUrl,
                    boardId = boardId
                )
                
                val response = apiService.createTask(request)
                
                if (response.success && response.data != null) {
                    // Sincronización exitosa: actualizar con ID del servidor
                    val serverTask = response.data.toDomain()
                    taskDao.updateServerIdAndSync(localId, serverTask.id)
                    
                    // Retornar tarea con el localId para mantener consistencia en la UI
                    val updatedEntity = savedEntity.copy(
                        serverId = serverTask.id,
                        isSynced = true,
                        createdBy = serverTask.createdBy
                    )
                    Result.success(updatedEntity.toDomain())
                } else {
                    // Error del servidor, pero la tarea está guardada localmente
                    Result.success(savedEntity.toDomain())
                }
            } catch (e: Exception) {
                // Error de red: la tarea está guardada localmente y se sincronizará después
                Result.success(savedEntity.toDomain())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateTask(
        id: Int,
        title: String,
        description: String?,
        status: TaskStatus,
        priority: TaskPriority,
        dueDate: String?,
        imageUrl: String?,
        boardId: Int?
    ): Result<Task> {
        return try {
            val now = LocalDateTime.now().format(dateFormatter)
            
            // Buscar tarea local
            val localTask = taskDao.getTaskByServerId(id) ?: taskDao.getTaskByLocalId(id)
            
            if (localTask != null) {
                // Actualizar entidad local
                val updatedEntity = localTask.copy(
                    title = title,
                    description = description,
                    status = status.name,
                    priority = priority.name,
                    dueDate = dueDate,
                    imageUrl = imageUrl,
                    boardId = boardId,
                    updatedAt = now,
                    isSynced = false // Marcar como no sincronizada
                )
                
                taskDao.updateTask(updatedEntity)
                
                // Intentar sincronizar con servidor
                try {
                    val request = UpdateTaskRequest(
                        title = title,
                        description = description,
                        status = status.name,
                        priority = priority.name,
                        dueDate = dueDate,
                        imageUrl = imageUrl,
                        boardId = boardId
                    )
                    
                    val serverId = localTask.serverId ?: id
                    val response = apiService.updateTask(serverId, request)
                    
                    if (response.success && response.data != null) {
                        taskDao.updateSyncStatus(localTask.localId, true)
                        Result.success(response.data.toDomain())
                    } else {
                        Result.success(updatedEntity.toDomain())
                    }
                } catch (e: Exception) {
                    // Error de red, pero actualización local exitosa
                    Result.success(updatedEntity.toDomain())
                }
            } else {
                Result.failure(Exception("Tarea no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteTask(id: Int): Result<Unit> {
        return try {
            // Buscar tarea local
            val localTask = taskDao.getTaskByServerId(id) ?: taskDao.getTaskByLocalId(id)
            
            if (localTask != null) {
                // Soft delete local
                taskDao.softDelete(localTask.localId)
                
                // Intentar eliminar en servidor
                try {
                    val serverId = localTask.serverId ?: id
                    val response = apiService.deleteTask(serverId)
                    
                    if (response.success) {
                        // Eliminación exitosa: hard delete local
                        taskDao.hardDelete(localTask.localId)
                    }
                    // Si falla, quedará marcada para sincronización posterior
                } catch (e: Exception) {
                    // Error de red, quedará marcada para eliminación posterior
                }
                
                Result.success(Unit)
            } else {
                Result.failure(Exception("Tarea no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateTaskStatus(id: Int, status: TaskStatus): Result<Task> {
        return try {
            val localTask = taskDao.getTaskByServerId(id) ?: taskDao.getTaskByLocalId(id)
            
            if (localTask != null) {
                val now = LocalDateTime.now().format(dateFormatter)
                val updatedEntity = localTask.copy(
                    status = status.name,
                    updatedAt = now,
                    isSynced = false
                )
                
                taskDao.updateTask(updatedEntity)
                
                // Intentar sincronizar
                try {
                    val serverId = localTask.serverId ?: id
                    val response = apiService.updateTaskStatus(serverId, status.name)
                    
                    if (response.success && response.data != null) {
                        taskDao.updateSyncStatus(localTask.localId, true)
                        Result.success(response.data.toDomain())
                    } else {
                        Result.success(updatedEntity.toDomain())
                    }
                } catch (e: Exception) {
                    Result.success(updatedEntity.toDomain())
                }
            } else {
                Result.failure(Exception("Tarea no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
