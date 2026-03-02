package com.miplan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplan.domain.model.Task
import com.miplan.domain.model.UiState
import com.miplan.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

/**
 * ViewModel para la pantalla de calendario
 */
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _tasksState = MutableStateFlow<UiState<List<Task>>>(UiState.Loading)
    val tasksState: StateFlow<UiState<List<Task>>> = _tasksState.asStateFlow()
    
    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth.asStateFlow()
    
    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()
    
    init {
        loadTasks()
    }
    
    /**
     * Carga todas las tareas del usuario
     */
    fun loadTasks() {
        viewModelScope.launch {
            _tasksState.value = UiState.Loading
            
            val result = taskRepository.getTasks()
            
            result.onSuccess { tasks ->
                _tasksState.value = UiState.Success(tasks)
            }.onFailure { error ->
                _tasksState.value = UiState.Error(error.message ?: "Error al cargar tareas")
            }
        }
    }
    
    /**
     * Obtiene las tareas de un día específico
     */
    fun getTasksForDate(date: LocalDate): List<Task> {
        val currentState = _tasksState.value
        if (currentState !is UiState.Success) return emptyList()
        
        return currentState.data.filter { task ->
            task.dueDate?.let { dueDate ->
                // Parsear la fecha de la tarea
                val taskDate = try {
                    if (dueDate.contains("T")) {
                        LocalDate.parse(dueDate.substringBefore("T"))
                    } else {
                        LocalDate.parse(dueDate)
                    }
                } catch (e: Exception) {
                    null
                }
                taskDate == date
            } ?: false
        }
    }
    
    /**
     * Obtiene el conteo de tareas por fecha
     */
    fun getTaskCountForDate(date: LocalDate): Int {
        return getTasksForDate(date).size
    }
    
    /**
     * Selecciona un mes
     */
    fun selectMonth(yearMonth: YearMonth) {
        _selectedMonth.value = yearMonth
    }
    
    /**
     * Navega al mes anterior
     */
    fun previousMonth() {
        _selectedMonth.value = _selectedMonth.value.minusMonths(1)
    }
    
    /**
     * Navega al mes siguiente
     */
    fun nextMonth() {
        _selectedMonth.value = _selectedMonth.value.plusMonths(1)
    }
    
    /**
     * Selecciona una fecha
     */
    fun selectDate(date: LocalDate?) {
        _selectedDate.value = date
    }
    
    /**
     * Vuelve al mes actual
     */
    fun goToToday() {
        _selectedMonth.value = YearMonth.now()
        _selectedDate.value = LocalDate.now()
    }
}
