package com.miplan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplan.data.repository.CollaboratorRepository
import com.miplan.domain.model.CollaboratorRole
import com.miplan.domain.model.TaskCollaborator
import com.miplan.domain.model.User
import com.miplan.domain.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para gestión de colaboradores de tareas
 */
@HiltViewModel
class CollaboratorViewModel @Inject constructor(
    private val collaboratorRepository: CollaboratorRepository
) : ViewModel() {
    
    // Estado de colaboradores
    private val _collaboratorsState = MutableStateFlow<UiState<List<TaskCollaborator>>>(UiState.Idle)
    val collaboratorsState: StateFlow<UiState<List<TaskCollaborator>>> = _collaboratorsState.asStateFlow()
    
    // Estado de agregar colaborador
    private val _addCollaboratorState = MutableStateFlow<UiState<TaskCollaborator>>(UiState.Idle)
    val addCollaboratorState: StateFlow<UiState<TaskCollaborator>> = _addCollaboratorState.asStateFlow()
    
    // Estado de búsqueda de usuario
    private val _searchUserState = MutableStateFlow<UiState<User?>>(UiState.Idle)
    val searchUserState: StateFlow<UiState<User?>> = _searchUserState.asStateFlow()
    
    // Estado de tareas compartidas
    private val _sharedTasksState = MutableStateFlow<UiState<List<Int>>>(UiState.Idle)
    val sharedTasksState: StateFlow<UiState<List<Int>>> = _sharedTasksState.asStateFlow()
    
    /**
     * Carga los colaboradores de una tarea
     */
    fun loadCollaborators(taskId: Int) {
        viewModelScope.launch {
            _collaboratorsState.value = UiState.Loading
            
            collaboratorRepository.getTaskCollaborators(taskId).fold(
                onSuccess = { collaborators ->
                    _collaboratorsState.value = UiState.Success(collaborators)
                },
                onFailure = { error ->
                    _collaboratorsState.value = UiState.Error(error.message ?: "Error al cargar colaboradores")
                }
            )
        }
    }
    
    /**
     * Agrega un colaborador a una tarea
     */
    fun addCollaborator(taskId: Int, userEmail: String, role: CollaboratorRole) {
        viewModelScope.launch {
            _addCollaboratorState.value = UiState.Loading
            
            collaboratorRepository.addCollaborator(taskId, userEmail, role).fold(
                onSuccess = { collaborator ->
                    _addCollaboratorState.value = UiState.Success(collaborator)
                    // Recargar lista de colaboradores
                    loadCollaborators(taskId)
                },
                onFailure = { error ->
                    _addCollaboratorState.value = UiState.Error(error.message ?: "Error al agregar colaborador")
                }
            )
        }
    }
    
    /**
     * Actualiza el rol de un colaborador
     */
    fun updateCollaboratorRole(taskId: Int, userId: Int, newRole: CollaboratorRole) {
        viewModelScope.launch {
            collaboratorRepository.updateCollaboratorRole(taskId, userId, newRole).fold(
                onSuccess = {
                    // Recargar lista de colaboradores
                    loadCollaborators(taskId)
                },
                onFailure = { error ->
                    _collaboratorsState.value = UiState.Error(error.message ?: "Error al actualizar rol")
                }
            )
        }
    }
    
    /**
     * Elimina un colaborador de una tarea
     */
    fun removeCollaborator(taskId: Int, userId: Int) {
        viewModelScope.launch {
            collaboratorRepository.removeCollaborator(taskId, userId).fold(
                onSuccess = {
                    // Recargar lista de colaboradores
                    loadCollaborators(taskId)
                },
                onFailure = { error ->
                    _collaboratorsState.value = UiState.Error(error.message ?: "Error al eliminar colaborador")
                }
            )
        }
    }
    
    /**
     * Busca un usuario por email
     */
    fun searchUserByEmail(email: String) {
        viewModelScope.launch {
            _searchUserState.value = UiState.Loading
            
            collaboratorRepository.searchUserByEmail(email).fold(
                onSuccess = { user ->
                    _searchUserState.value = UiState.Success(user)
                },
                onFailure = { error ->
                    _searchUserState.value = UiState.Error(error.message ?: "Error al buscar usuario")
                }
            )
        }
    }
    
    /**
     * Carga las tareas compartidas con el usuario actual
     */
    fun loadSharedTasks() {
        viewModelScope.launch {
            _sharedTasksState.value = UiState.Loading
            
            collaboratorRepository.getSharedTasks().fold(
                onSuccess = { taskIds ->
                    _sharedTasksState.value = UiState.Success(taskIds)
                },
                onFailure = { error ->
                    _sharedTasksState.value = UiState.Error(error.message ?: "Error al cargar tareas compartidas")
                }
            )
        }
    }
    
    /**
     * Resetea el estado de agregar colaborador
     */
    fun resetAddCollaboratorState() {
        _addCollaboratorState.value = UiState.Idle
    }
    
    /**
     * Resetea el estado de búsqueda de usuario
     */
    fun resetSearchUserState() {
        _searchUserState.value = UiState.Idle
    }
}
