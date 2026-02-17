package com.miplan.data.repository

import com.miplan.data.remote.ApiService
import com.miplan.data.remote.dto.request.CreateTaskRequest
import com.miplan.data.remote.dto.request.UpdateTaskRequest
import com.miplan.domain.model.Task
import com.miplan.domain.model.TaskPriority
import com.miplan.domain.model.TaskStatus
import com.miplan.domain.repository.TaskRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio de tareas
 */
@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : TaskRepository {
    
    override suspend fun getTasks(): Result<List<Task>> {
        return try {
            val response = apiService.getTasks()
            
            if (response.success && response.data != null) {
                val tasks = response.data.map { it.toDomain() }
                Result.success(tasks)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTaskById(id: Int): Result<Task> {
        return try {
            val response = apiService.getTaskById(id)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTasksByBoard(boardId: Int): Result<List<Task>> {
        return try {
            val response = apiService.getTasksByBoard(boardId)
            
            if (response.success && response.data != null) {
                val tasks = response.data.map { it.toDomain() }
                Result.success(tasks)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTasksByStatus(status: TaskStatus): Result<List<Task>> {
        return try {
            val response = apiService.getTasksByStatus(status.name)
            
            if (response.success && response.data != null) {
                val tasks = response.data.map { it.toDomain() }
                Result.success(tasks)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTasksByDate(date: String): Result<List<Task>> {
        return try {
            val response = apiService.getTasksByDate(date)
            
            if (response.success && response.data != null) {
                val tasks = response.data.map { it.toDomain() }
                Result.success(tasks)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createTask(
        title: String,
        description: String?,
        priority: TaskPriority,
        dueDate: String?,
        boardId: Int?
    ): Result<Task> {
        return try {
            val request = CreateTaskRequest(
                title = title,
                description = description,
                priority = priority.name,
                dueDate = dueDate,
                boardId = boardId
            )
            
            val response = apiService.createTask(request)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
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
        boardId: Int?
    ): Result<Task> {
        return try {
            val request = UpdateTaskRequest(
                title = title,
                description = description,
                status = status.name,
                priority = priority.name,
                dueDate = dueDate,
                boardId = boardId
            )
            
            val response = apiService.updateTask(id, request)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteTask(id: Int): Result<Unit> {
        return try {
            val response = apiService.deleteTask(id)
            
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateTaskStatus(id: Int, status: TaskStatus): Result<Task> {
        return try {
            val response = apiService.updateTaskStatus(id, status.name)
            
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
