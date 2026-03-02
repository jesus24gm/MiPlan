package com.miplan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miplan.data.repository.AdminRepository
import com.miplan.domain.model.AdminStats
import com.miplan.domain.model.Role
import com.miplan.domain.model.UiState
import com.miplan.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de administración
 */
@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {
    
    // Estado de la lista de usuarios
    private val _usersState = MutableStateFlow<UiState<List<User>>>(UiState.Idle)
    val usersState: StateFlow<UiState<List<User>>> = _usersState.asStateFlow()
    
    // Estado de las estadísticas
    private val _statsState = MutableStateFlow<UiState<AdminStats>>(UiState.Idle)
    val statsState: StateFlow<UiState<AdminStats>> = _statsState.asStateFlow()
    
    // Estado de actualización de rol
    private val _updateRoleState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val updateRoleState: StateFlow<UiState<User>> = _updateRoleState.asStateFlow()
    
    /**
     * Carga la lista de todos los usuarios
     */
    fun loadAllUsers() {
        viewModelScope.launch {
            _usersState.value = UiState.Loading
            
            val result = adminRepository.getAllUsers()
            
            _usersState.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al cargar usuarios")
            }
        }
    }
    
    /**
     * Carga las estadísticas del sistema
     */
    fun loadStats() {
        viewModelScope.launch {
            _statsState.value = UiState.Loading
            
            val result = adminRepository.getAdminStats()
            
            _statsState.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al cargar estadísticas")
            }
        }
    }
    
    /**
     * Actualiza el rol de un usuario
     */
    fun updateUserRole(userId: Int, newRole: Role) {
        viewModelScope.launch {
            _updateRoleState.value = UiState.Loading
            
            val result = adminRepository.updateUserRole(userId, newRole)
            
            _updateRoleState.value = if (result.isSuccess) {
                // Recargar la lista de usuarios
                loadAllUsers()
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Error al actualizar rol")
            }
        }
    }
    
    /**
     * Resetea el estado de actualización de rol
     */
    fun resetUpdateRoleState() {
        _updateRoleState.value = UiState.Idle
    }
    
    /**
     * Suspende o activa una cuenta de usuario
     */
    fun toggleUserStatus(userId: Int, isActive: Boolean) {
        viewModelScope.launch {
            val result = adminRepository.toggleUserStatus(userId, isActive)
            
            if (result.isSuccess) {
                // Recargar la lista de usuarios
                loadAllUsers()
            }
        }
    }
    
    /**
     * Elimina un usuario del sistema
     */
    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            val result = adminRepository.deleteUser(userId)
            
            if (result.isSuccess) {
                // Recargar la lista de usuarios
                loadAllUsers()
            }
        }
    }
}
