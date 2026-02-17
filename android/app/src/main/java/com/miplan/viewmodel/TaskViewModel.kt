package com.miplan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplan.domain.model.Task
import com.miplan.domain.model.TaskPriority
import com.miplan.domain.model.TaskStatus
import com.miplan.domain.model.UiState
import com.miplan.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para gestión de tareas
 */
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _tasksState = MutableStateFlow<UiState<List<Task>>>(UiState.Idle)
    val tasksState: StateFlow<UiState<List<Task>>> = _tasksState.asStateFlow()
    
    private val _taskDetailState = MutableStateFlow<UiState<Task>>(UiState.Idle)
    val taskDetailState: StateFlow<UiState<Task>> = _taskDetailState.asStateFlow()
    
    private val _createTaskState = MutableStateFlow<UiState<Task>>(UiState.Idle)
    val createTaskState: StateFlow<UiState<Task>> = _createTaskState.asStateFlow()
    
    private val _updateTaskState = MutableStateFlow<UiState<Task>>(UiState.Idle)
    val updateTaskState: StateFlow<UiState<Task>> = _updateTaskState.asStateFlow()
    
    private val _deleteTaskState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteTaskState: StateFlow<UiState<Unit>> = _deleteTaskState.asStateFlow()
    
    /**
     * Carga todas las tareas del usuario
     */
    fun loadTasks() {
        viewModelScope.launch {
            _tasksState.value = UiState.Loading
            
            val result = taskRepository.getTasks()
            
            _tasksState.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al cargar tareas")
            }
        }
    }
    
    /**
     * Carga una tarea por ID
     */
    fun loadTaskById(id: Int) {
        viewModelScope.launch {
            _taskDetailState.value = UiState.Loading
            
            val result = taskRepository.getTaskById(id)
            
            _taskDetailState.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al cargar tarea")
            }
        }
    }
    
    /**
     * Carga tareas por tablero
     */
    fun loadTasksByBoard(boardId: Int) {
        viewModelScope.launch {
            _tasksState.value = UiState.Loading
            
            val result = taskRepository.getTasksByBoard(boardId)
            
            _tasksState.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al cargar tareas")
            }
        }
    }
    
    /**
     * Carga tareas por estado
     */
    fun loadTasksByStatus(status: TaskStatus) {
        viewModelScope.launch {
            _tasksState.value = UiState.Loading
            
            val result = taskRepository.getTasksByStatus(status)
            
            _tasksState.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al cargar tareas")
            }
        }
    }
    
    /**
     * Carga tareas por fecha
     */
    fun loadTasksByDate(date: String) {
        viewModelScope.launch {
            _tasksState.value = UiState.Loading
            
            val result = taskRepository.getTasksByDate(date)
            
            _tasksState.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al cargar tareas")
            }
        }
    }
    
    /**
     * Crea una nueva tarea
     */
    fun createTask(
        title: String,
        description: String?,
        priority: TaskPriority,
        dueDate: String?,
        boardId: Int?
    ) {
        viewModelScope.launch {
            _createTaskState.value = UiState.Loading
            
            val result = taskRepository.createTask(title, description, priority, dueDate, boardId)
            
            _createTaskState.value = if (result.isSuccess) {
                loadTasks() // Recargar lista
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al crear tarea")
            }
        }
    }
    
    /**
     * Actualiza una tarea existente
     */
    fun updateTask(
        id: Int,
        title: String,
        description: String?,
        status: TaskStatus,
        priority: TaskPriority,
        dueDate: String?,
        boardId: Int?
    ) {
        viewModelScope.launch {
            _updateTaskState.value = UiState.Loading
            
            val result = taskRepository.updateTask(id, title, description, status, priority, dueDate, boardId)
            
            _updateTaskState.value = if (result.isSuccess) {
                loadTasks() // Recargar lista
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al actualizar tarea")
            }
        }
    }
    
    /**
     * Elimina una tarea
     */
    fun deleteTask(id: Int) {
        viewModelScope.launch {
            _deleteTaskState.value = UiState.Loading
            
            val result = taskRepository.deleteTask(id)
            
            _deleteTaskState.value = if (result.isSuccess) {
                loadTasks() // Recargar lista
                UiState.Success(Unit)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al eliminar tarea")
            }
        }
    }
    
    /**
     * Actualiza el estado de una tarea
     */
    fun updateTaskStatus(id: Int, status: TaskStatus) {
        viewModelScope.launch {
            val result = taskRepository.updateTaskStatus(id, status)
            
            if (result.isSuccess) {
                loadTasks() // Recargar lista
            }
        }
    }
    
    /**
     * Resetea estados
     */
    fun resetCreateState() {
        _createTaskState.value = UiState.Idle
    }
    
    fun resetUpdateState() {
        _updateTaskState.value = UiState.Idle
    }
    
    fun resetDeleteState() {
        _deleteTaskState.value = UiState.Idle
    }
}
