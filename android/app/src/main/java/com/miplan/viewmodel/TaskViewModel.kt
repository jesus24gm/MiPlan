package com.miplan.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplan.domain.model.Task
import com.miplan.domain.model.TaskPriority
import com.miplan.domain.model.TaskStatus
import com.miplan.domain.model.UiState
import com.miplan.domain.repository.TaskRepository
import com.miplan.notifications.NotificationHelper
import com.miplan.notifications.NotificationPreferences
import com.miplan.notifications.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * ViewModel para gestión de tareas
 */
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val application: Application
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
                val task = result.getOrNull()!!
                
                // Mostrar notificación de confirmación y programar notificaciones
                showTaskCreatedNotification(task)
                
                loadTasks() // Recargar lista
                UiState.Success(task)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al crear tarea")
            }
        }
    }
    
    /**
     * Muestra notificación de tarea creada y programa notificaciones futuras
     */
    private fun showTaskCreatedNotification(task: Task) {
        val preferences = NotificationPreferences(application)
        
        // Extraer fecha y hora del dueDate
        val (date, time) = parseDueDate(task.dueDate)
        
        // Mostrar notificación inmediata de confirmación
        if (preferences.taskCreatedNotificationEnabled && preferences.notificationsEnabled) {
            NotificationHelper.showTaskCreatedNotification(
                application,
                task.id,
                task.title,
                date,
                time
            )
        }
        
        // Programar notificaciones futuras
        if (date != null && preferences.notificationsEnabled) {
            NotificationScheduler.scheduleTaskNotifications(
                application,
                task.id,
                task.title,
                date,
                time
            )
        }
    }
    
    /**
     * Parsea el dueDate en formato ISO a fecha y hora separados
     * Retorna Pair(fecha, hora) o Pair(null, null) si no hay fecha
     */
    private fun parseDueDate(dueDate: String?): Pair<String?, String?> {
        if (dueDate == null) return Pair(null, null)
        
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val dateTime = LocalDateTime.parse(dueDate, DateTimeFormatter.ISO_DATE_TIME)
                val date = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                Pair(date, time)
            } else {
                // Fallback para versiones antiguas
                val parts = dueDate.split("T")
                if (parts.size >= 2) {
                    val date = parts[0]
                    val time = parts[1].substring(0, 5) // HH:mm
                    Pair(date, time)
                } else {
                    Pair(parts[0], null)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(null, null)
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
                // Cancelar notificaciones programadas
                NotificationScheduler.cancelTaskNotifications(application, id)
                
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
