package com.miplan.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplan.domain.model.Task
import com.miplan.domain.model.TaskPriority
import com.miplan.domain.model.TaskStatus
import com.miplan.domain.model.UiState
import com.miplan.domain.repository.TaskRepository
import com.miplan.notifications.NotificationScheduler
import com.miplan.notifications.NotificationHelper
import com.miplan.data.preferences.NotificationPreferences
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
    private val application: Application,
    private val notificationPreferences: NotificationPreferences
) : ViewModel() {
    
    private val notificationScheduler = NotificationScheduler(application, notificationPreferences)
    
    private val _tasksState = MutableStateFlow<UiState<List<Task>>>(UiState.Idle)
    val tasksState: StateFlow<UiState<List<Task>>> = _tasksState.asStateFlow()
    
    private val _boardTasksState = MutableStateFlow<UiState<List<Task>>>(UiState.Idle)
    val boardTasksState: StateFlow<UiState<List<Task>>> = _boardTasksState.asStateFlow()
    
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
            _boardTasksState.value = UiState.Loading
            
            val result = taskRepository.getTasksByBoard(boardId)
            
            _boardTasksState.value = if (result.isSuccess) {
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
        imageUrl: String?,
        boardId: Int?
    ) {
        viewModelScope.launch {
            _createTaskState.value = UiState.Loading
            
            val result = taskRepository.createTask(title, description, priority, dueDate, imageUrl, boardId)
            
            _createTaskState.value = if (result.isSuccess) {
                val task = result.getOrNull()!!
                println("🔔 Tarea creada exitosamente: ${task.title}, ID: ${task.id}")
                println("🔔 DueDate: $dueDate, BoardId: $boardId")
                
                // SIEMPRE mostrar notificación de confirmación
                try {
                    if (!dueDate.isNullOrBlank()) {
                        // Parsear fecha con manejo de diferentes formatos
                        val dueDateParsed = parseDueDate(dueDate)
                        val hasTime = dueDate.contains(":")
                        
                        // Programar notificaciones si están habilitadas
                        if (notificationPreferences.taskNotificationsEnabled) {
                            println("🔔 Programando notificaciones para: $dueDateParsed, hasTime: $hasTime")
                            notificationScheduler.scheduleTaskNotifications(
                                taskId = task.id,
                                dueDate = dueDateParsed,
                                hasSpecificTime = hasTime
                            )
                        }
                        
                        // Mostrar notificación de confirmación con fecha
                        println("🔔 Mostrando notificación de confirmación con fecha...")
                        showTaskCreatedNotification(task, dueDateParsed, hasTime, boardId)
                    } else {
                        // Mostrar notificación sin fecha
                        println("🔔 Mostrando notificación de confirmación sin fecha...")
                        showTaskCreatedNotificationWithoutDate(task, boardId)
                    }
                } catch (e: Exception) {
                    println("❌ Error al mostrar notificación: ${e.message}")
                    e.printStackTrace()
                }
                
                loadTasks() // Recargar lista
                UiState.Success(task)
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
        imageUrl: String?,
        boardId: Int?
    ) {
        viewModelScope.launch {
            _updateTaskState.value = UiState.Loading
            
            val result = taskRepository.updateTask(id, title, description, status, priority, dueDate, imageUrl, boardId)
            
            _updateTaskState.value = if (result.isSuccess) {
                val task = result.getOrNull()!!
                println("🔔 Tarea actualizada: ${task.title}, ID: ${task.id}")
                println("🔔 Nueva DueDate: $dueDate, BoardId: $boardId")
                
                // Re-programar notificaciones y mostrar confirmación
                if (status == TaskStatus.COMPLETED) {
                    // Si se completó, cancelar notificaciones
                    notificationScheduler.cancelTaskNotifications(id)
                } else if (!dueDate.isNullOrBlank()) {
                    try {
                        val dueDateParsed = parseDueDate(dueDate)
                        val hasTime = dueDate.contains(":")
                        
                        // Programar notificaciones si están habilitadas
                        if (notificationPreferences.taskNotificationsEnabled) {
                            notificationScheduler.scheduleTaskNotifications(
                                taskId = id,
                                dueDate = dueDateParsed,
                                hasSpecificTime = hasTime
                            )
                        }
                        
                        // Mostrar notificación de confirmación
                        println("🔔 Mostrando notificación de tarea actualizada con fecha...")
                        showTaskUpdatedNotification(task, dueDateParsed, hasTime, boardId)
                    } catch (e: Exception) {
                        println("❌ Error al actualizar notificaciones: ${e.message}")
                        e.printStackTrace()
                    }
                } else {
                    // Si no tiene fecha, cancelar notificaciones
                    notificationScheduler.cancelTaskNotifications(id)
                }
                
                loadTasks() // Recargar lista
                UiState.Success(task)
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
                // Cancelar notificaciones de la tarea eliminada
                notificationScheduler.cancelTaskNotifications(id)
                
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
     * Actualiza el estado de una tarea (versión con String)
     */
    fun updateTaskStatus(id: Int, statusString: String) {
        viewModelScope.launch {
            _updateTaskState.value = UiState.Loading
            
            val status = try {
                TaskStatus.valueOf(statusString)
            } catch (e: Exception) {
                _updateTaskState.value = UiState.Error("Estado inválido")
                return@launch
            }
            
            val result = taskRepository.updateTaskStatus(id, status)
            
            _updateTaskState.value = if (result.isSuccess) {
                loadTasks() // Recargar lista
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al actualizar estado")
            }
        }
    }
    
    /**
     * Resetea el estado de actualización
     */
    fun resetUpdateTaskState() {
        _updateTaskState.value = UiState.Idle
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
    
    /**
     * Muestra notificación de confirmación al crear tarea con fecha
     */
    private fun showTaskCreatedNotification(task: Task, dueDate: LocalDateTime, hasTime: Boolean, boardId: Int?) {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        
        val formattedDate = dueDate.format(dateFormatter)
        val formattedTime = if (hasTime) dueDate.format(timeFormatter) else null
        
        NotificationHelper.showTaskCreatedNotification(
            context = application,
            taskId = task.id,
            taskTitle = task.title,
            dueDate = formattedDate,
            dueTime = formattedTime,
            boardId = boardId
        )
    }
    
    /**
     * Muestra notificación de confirmación al crear tarea sin fecha
     */
    private fun showTaskCreatedNotificationWithoutDate(task: Task, boardId: Int?) {
        NotificationHelper.showTaskCreatedNotificationWithoutDate(
            context = application,
            taskId = task.id,
            taskTitle = task.title,
            boardId = boardId
        )
    }
    
    /**
     * Muestra notificación al actualizar tarea con fecha
     */
    private fun showTaskUpdatedNotification(task: Task, dueDate: LocalDateTime, hasTime: Boolean, boardId: Int?) {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        
        val formattedDate = dueDate.format(dateFormatter)
        val formattedTime = if (hasTime) dueDate.format(timeFormatter) else null
        
        NotificationHelper.showTaskUpdatedNotification(
            context = application,
            taskId = task.id,
            taskTitle = task.title,
            dueDate = formattedDate,
            dueTime = formattedTime,
            boardId = boardId
        )
    }
    
    /**
     * Parsea una fecha en diferentes formatos a LocalDateTime
     * Soporta:
     * - "2026-03-02T19:00:00" (ISO con hora)
     * - "2026-03-02 19:00:00" (con espacio y hora)
     * - "2026-03-02" (solo fecha, agrega 00:00:00)
     */
    private fun parseDueDate(dueDate: String): LocalDateTime {
        return try {
            when {
                // Formato ISO con T: "2026-03-02T19:00:00"
                dueDate.contains("T") -> {
                    LocalDateTime.parse(dueDate.substringBefore("."))
                }
                // Formato con espacio: "2026-03-02 19:00:00"
                dueDate.contains(" ") -> {
                    LocalDateTime.parse(dueDate.replace(" ", "T").substringBefore("."))
                }
                // Solo fecha: "2026-03-02" -> agregar hora 00:00:00
                else -> {
                    LocalDateTime.parse("${dueDate}T00:00:00")
                }
            }
        } catch (e: Exception) {
            println("❌ Error al parsear fecha '$dueDate': ${e.message}")
            // Fallback: intentar agregar hora por defecto
            LocalDateTime.parse("${dueDate}T00:00:00")
        }
    }
}
