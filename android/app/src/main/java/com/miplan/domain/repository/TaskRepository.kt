package com.miplan.domain.repository

import com.miplan.domain.model.Task
import com.miplan.domain.model.TaskPriority
import com.miplan.domain.model.TaskStatus

/**
 * Interface del repositorio de tareas
 */
interface TaskRepository {
    
    /**
     * Obtiene todas las tareas del usuario actual
     */
    suspend fun getTasks(): Result<List<Task>>
    
    /**
     * Obtiene una tarea por su ID
     */
    suspend fun getTaskById(id: Int): Result<Task>
    
    /**
     * Obtiene tareas por tablero
     */
    suspend fun getTasksByBoard(boardId: Int): Result<List<Task>>
    
    /**
     * Obtiene tareas por estado
     */
    suspend fun getTasksByStatus(status: TaskStatus): Result<List<Task>>
    
    /**
     * Obtiene tareas por fecha
     */
    suspend fun getTasksByDate(date: String): Result<List<Task>>
    
    /**
     * Crea una nueva tarea
     */
    suspend fun createTask(
        title: String,
        description: String?,
        priority: TaskPriority,
        dueDate: String?,
        boardId: Int?
    ): Result<Task>
    
    /**
     * Actualiza una tarea existente
     */
    suspend fun updateTask(
        id: Int,
        title: String,
        description: String?,
        status: TaskStatus,
        priority: TaskPriority,
        dueDate: String?,
        boardId: Int?
    ): Result<Task>
    
    /**
     * Elimina una tarea
     */
    suspend fun deleteTask(id: Int): Result<Unit>
    
    /**
     * Cambia el estado de una tarea
     */
    suspend fun updateTaskStatus(id: Int, status: TaskStatus): Result<Task>
}
