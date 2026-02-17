package com.miplan.services

import com.miplan.models.responses.TaskResponse
import com.miplan.repositories.TaskRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Servicio de tareas
 */
class TaskService(
    private val taskRepository: TaskRepository
) {
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    /**
     * Obtiene todas las tareas de un usuario
     */
    suspend fun getUserTasks(userId: Int): List<TaskResponse> {
        val tasks = taskRepository.findByUserId(userId)
        return tasks.map { task ->
            val boardName = taskRepository.getBoardName(task.boardId)
            TaskResponse(
                id = task.id,
                title = task.title,
                description = task.description,
                status = task.status,
                priority = task.priority,
                dueDate = task.dueDate?.format(dateFormatter),
                boardId = task.boardId,
                boardName = boardName,
                createdBy = task.createdBy,
                createdAt = task.createdAt.format(dateFormatter),
                updatedAt = task.updatedAt.format(dateFormatter)
            )
        }
    }
    
    /**
     * Obtiene una tarea por ID
     */
    suspend fun getTaskById(taskId: Int, userId: Int): TaskResponse {
        val task = taskRepository.findById(taskId)
            ?: throw IllegalArgumentException("Tarea no encontrada")
        
        // Verificar que la tarea pertenezca al usuario
        if (!taskRepository.isTaskOwnedByUser(taskId, userId)) {
            throw IllegalArgumentException("No tienes permiso para ver esta tarea")
        }
        
        val boardName = taskRepository.getBoardName(task.boardId)
        
        return TaskResponse(
            id = task.id,
            title = task.title,
            description = task.description,
            status = task.status,
            priority = task.priority,
            dueDate = task.dueDate?.format(dateFormatter),
            boardId = task.boardId,
            boardName = boardName,
            createdBy = task.createdBy,
            createdAt = task.createdAt.format(dateFormatter),
            updatedAt = task.updatedAt.format(dateFormatter)
        )
    }
    
    /**
     * Obtiene tareas por tablero
     */
    suspend fun getTasksByBoard(boardId: Int, userId: Int): List<TaskResponse> {
        val tasks = taskRepository.findByBoardId(boardId)
        
        return tasks.filter { it.createdBy == userId }.map { task ->
            val boardName = taskRepository.getBoardName(task.boardId)
            TaskResponse(
                id = task.id,
                title = task.title,
                description = task.description,
                status = task.status,
                priority = task.priority,
                dueDate = task.dueDate?.format(dateFormatter),
                boardId = task.boardId,
                boardName = boardName,
                createdBy = task.createdBy,
                createdAt = task.createdAt.format(dateFormatter),
                updatedAt = task.updatedAt.format(dateFormatter)
            )
        }
    }
    
    /**
     * Obtiene tareas por estado
     */
    suspend fun getTasksByStatus(userId: Int, status: String): List<TaskResponse> {
        val tasks = taskRepository.findByStatus(userId, status)
        return tasks.map { task ->
            val boardName = taskRepository.getBoardName(task.boardId)
            TaskResponse(
                id = task.id,
                title = task.title,
                description = task.description,
                status = task.status,
                priority = task.priority,
                dueDate = task.dueDate?.format(dateFormatter),
                boardId = task.boardId,
                boardName = boardName,
                createdBy = task.createdBy,
                createdAt = task.createdAt.format(dateFormatter),
                updatedAt = task.updatedAt.format(dateFormatter)
            )
        }
    }
    
    /**
     * Obtiene tareas por fecha
     */
    suspend fun getTasksByDate(userId: Int, dateStr: String): List<TaskResponse> {
        val date = LocalDateTime.parse(dateStr, dateFormatter)
        val tasks = taskRepository.findByDate(userId, date)
        
        return tasks.map { task ->
            val boardName = taskRepository.getBoardName(task.boardId)
            TaskResponse(
                id = task.id,
                title = task.title,
                description = task.description,
                status = task.status,
                priority = task.priority,
                dueDate = task.dueDate?.format(dateFormatter),
                boardId = task.boardId,
                boardName = boardName,
                createdBy = task.createdBy,
                createdAt = task.createdAt.format(dateFormatter),
                updatedAt = task.updatedAt.format(dateFormatter)
            )
        }
    }
    
    /**
     * Crea una nueva tarea
     */
    suspend fun createTask(
        userId: Int,
        title: String,
        description: String?,
        priority: String,
        dueDateStr: String?,
        boardId: Int?
    ): TaskResponse {
        // Validaciones
        if (title.isBlank()) {
            throw IllegalArgumentException("El título es obligatorio")
        }
        if (title.length > 200) {
            throw IllegalArgumentException("El título es demasiado largo")
        }
        
        val dueDate = dueDateStr?.let { LocalDateTime.parse(it, dateFormatter) }
        
        val task = taskRepository.create(
            title = title,
            description = description,
            priority = priority,
            dueDate = dueDate,
            boardId = boardId,
            createdBy = userId
        ) ?: throw Exception("Error al crear tarea")
        
        val boardName = taskRepository.getBoardName(task.boardId)
        
        return TaskResponse(
            id = task.id,
            title = task.title,
            description = task.description,
            status = task.status,
            priority = task.priority,
            dueDate = task.dueDate?.format(dateFormatter),
            boardId = task.boardId,
            boardName = boardName,
            createdBy = task.createdBy,
            createdAt = task.createdAt.format(dateFormatter),
            updatedAt = task.updatedAt.format(dateFormatter)
        )
    }
    
    /**
     * Actualiza una tarea
     */
    suspend fun updateTask(
        taskId: Int,
        userId: Int,
        title: String,
        description: String?,
        status: String,
        priority: String,
        dueDateStr: String?,
        boardId: Int?
    ): TaskResponse {
        // Verificar que la tarea pertenezca al usuario
        if (!taskRepository.isTaskOwnedByUser(taskId, userId)) {
            throw IllegalArgumentException("No tienes permiso para editar esta tarea")
        }
        
        // Validaciones
        if (title.isBlank()) {
            throw IllegalArgumentException("El título es obligatorio")
        }
        
        val dueDate = dueDateStr?.let { LocalDateTime.parse(it, dateFormatter) }
        
        taskRepository.update(
            id = taskId,
            title = title,
            description = description,
            status = status,
            priority = priority,
            dueDate = dueDate,
            boardId = boardId
        )
        
        return getTaskById(taskId, userId)
    }
    
    /**
     * Actualiza el estado de una tarea
     */
    suspend fun updateTaskStatus(taskId: Int, userId: Int, status: String): TaskResponse {
        // Verificar que la tarea pertenezca al usuario
        if (!taskRepository.isTaskOwnedByUser(taskId, userId)) {
            throw IllegalArgumentException("No tienes permiso para editar esta tarea")
        }
        
        taskRepository.updateStatus(taskId, status)
        
        return getTaskById(taskId, userId)
    }
    
    /**
     * Elimina una tarea
     */
    suspend fun deleteTask(taskId: Int, userId: Int) {
        // Verificar que la tarea pertenezca al usuario
        if (!taskRepository.isTaskOwnedByUser(taskId, userId)) {
            throw IllegalArgumentException("No tienes permiso para eliminar esta tarea")
        }
        
        taskRepository.delete(taskId)
    }
}
